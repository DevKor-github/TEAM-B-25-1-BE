package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.domain.*;
import com.ODG.ODG_back.domain.enums.TransportType;
import com.ODG.ODG_back.dto.external.OverpassStationInfoDTO;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.external.google.GoogleMatrixApiClient;
import com.ODG.ODG_back.external.openStreetMap.OpenStreetMapApiClient;
import com.ODG.ODG_back.external.overpass.OverpassApiClient;
import com.ODG.ODG_back.mapper.MidpointMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.MidpointRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import com.ODG.ODG_back.repository.SubwayDurationTimeRepository;
import com.ODG.ODG_back.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component("subwayStrategy")
@RequiredArgsConstructor
@Slf4j
public class SubwayMidpointStrategy implements MidpointStrategy {
    private final MeetingRepository meetingRepository;
    private final RecommendedMidpointRepository recommendedMidpointRepository;
    private final MidpointRepository midpointRepository;
    private final MidpointMapper midpointMapper;
    private final SubwayDurationTimeRepository timeRepository; // 지하철역간 이동 시간 저장소
    private final GoogleMatrixApiClient matrixApiClient; // 소요 시간 API 요청
    private final AuthService authService;


    @Override
    public MidpointResponseDto calculateMidpoints(String inviteCode) {
        log.info("Calculating midpoints for inviteCode: {}", inviteCode);

        Meeting meeting = meetingRepository.findByInviteCode(inviteCode).orElseThrow(
                () -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND)
        );
        // 출발지 = 참가자 위치
        List<Participant> participants = meeting.getParticipants();
        // 목적지 = 모든 지하철역
        List<Midpoint> allMidpoints = midpointRepository.findAll();
        log.info("Number of participants: {}, number of midpoints: {}", participants.size(),
                allMidpoints.size());

        List<Midpoint> candidates = hubNearCenter(participants, allMidpoints);

        TimeMatrix tm = buildTimeMatrix(participants, candidates);
        int[][] timeMatrix = tm.matrix();
        List<Participant> rowOrder = tm.rowOrder();
        log.info("Time matrix dimensions: {} participants × {} midpoints", timeMatrix.length,
                timeMatrix[0].length);

        MidpointScore best = scoreMidpoints(timeMatrix, candidates, participants.size(), rowOrder);
        log.info("Best midpoint: {}, average time: {}", best.midpoint().getName(), best.avg());

        Long currentPid = authService.getCurrentParticipantId(meeting);
        int rowIndex = findRowIndex(rowOrder, currentPid);
        int selfTime = best.times()[rowIndex];

        saveRecommendedMidpoint(best, meeting);
        return midpointMapper.toDtoSelfOnly(
                best.midpoint(),
                best.avg(),
                best.totalDeviation(),
                currentPid,
                selfTime,
                participants.size()
        );
    }

    Midpoint getNearbySubwayStationMidpoint(Participant participant) {
        List<Midpoint> allMidpoints = midpointRepository.findAll();
        BigDecimal minDistance = null;
        Midpoint nearest = null;

        BigDecimal lat = participant.getLatitude();
        BigDecimal lon = participant.getLongitude();

        for (Midpoint midpoint : allMidpoints) {
            BigDecimal stationLat = midpoint.getLatitude();
            BigDecimal stationLng = midpoint.getLongitude();

            BigDecimal LatDiff = lat.subtract(stationLat);
            BigDecimal LngDiff = lon.subtract(stationLng);

            BigDecimal distancePow = BigDecimal.valueOf(Math.pow(LatDiff.doubleValue(), 2) + Math.pow(LngDiff.doubleValue(), 2));

            if (nearest == null || distancePow.compareTo(minDistance) < 0) {
                minDistance = distancePow;
                nearest = midpoint;
            }
        }

        if (nearest == null) {
            throw new NotFoundException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return nearest;
    }

    private TimeMatrix buildTimeMatrix(List<Participant> participants, List<Midpoint> candidates) {
        int[][] timeMatrix = new int[participants.size()][candidates.size()];

        // transportType 기준으로 그룹화하되, 키 순서를 안정화(TreeMap)하고, 각 그룹 내부는 id 기준으로 정렬
        Map<TransportType, List<Participant>> grouped = participants.stream()
                .collect(groupingBy(Participant::getTransportType, TreeMap::new,
                        Collectors.toList()));
        List<Participant> rowOrder = new ArrayList<>(participants.size());

        int rowIndex = 0;

        for (var entry : grouped.entrySet()) {
            int[][] groupMatrix;

            List<Participant> group = entry.getValue().stream()
                    .sorted(Comparator.comparing(Participant::getId))
                    .toList();

            List<String> origins = group.stream()
                    .map(p -> p.getLatitude() + "," + p.getLongitude())
                    .toList();

            if(entry.getKey() == TransportType.CAR) {
                List<String> destinations = candidates.stream()
                        .map(m -> m.getLatitude() + "," + m.getLongitude())
                        .toList();

                groupMatrix = matrixApiClient.getTimeMatrix(origins, destinations,
                        entry.getKey());
            }
            else{
                groupMatrix = new int[group.size()][candidates.size()];

                for(int i = 0 ; i < participants.size() ; i++){
                    for(int j = 0 ; j < candidates.size() ; j++){
                        SubwayDurationTime durationTime = getDurationInfo(
                                getNearbySubwayStationMidpoint(participants.get(i)),
                                candidates.get(j)
                        );

                        groupMatrix[i][j] = durationTime.getShortestDurationTime() + getWalkTimeToStation(participants.get(i), getNearbySubwayStationMidpoint(participants.get(i)));
                    }
                }
            }

            for (int i = 0; i < group.size(); i++) {
                timeMatrix[rowIndex] = groupMatrix[i];
                rowOrder.add(group.get(i)); // 행과 참가자 매핑 기록
                rowIndex++;
            }
        }
        return new TimeMatrix(timeMatrix, rowOrder);
    }

    private int getWalkTimeToStation(Participant p, Midpoint m) {
        double distance = haversine(p.getLatitude().doubleValue(), p.getLongitude().doubleValue(),
                m.getLatitude().doubleValue(), m.getLongitude().doubleValue());
        return (int) ((distance * 1000) / 80); // 도보 속도 80m/min 가정
    }

    public SubwayDurationTime getDurationInfo(
            Midpoint start,
            Midpoint end) {
        // 시작역과 목적지역 이름이 모두 존재해야 함

        if (Objects.equals(start.getId(), end.getId())) {
            new SubwayDurationTime();
            return SubwayDurationTime.builder()
                    .start(start.getId())
                    .end(end.getId())
                    .shortestDurationTime(0)
                    .transferCountForShortestDuration(0)
                    .build();
        }
        Optional<SubwayDurationTime> durationTime = timeRepository.findByStartAndEnd(
                start.getId(), end.getId());

        if (durationTime.isEmpty()) {
            throw new NotFoundException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return durationTime.get();
    }

    private void saveRecommendedMidpoint(MidpointScore best, Meeting meeting) {
        RecommendedMidpoint recommended = new RecommendedMidpoint(
                null,
                best.avg(),
                1,
                LocalDateTime.now(),
                meeting,
                best.midpoint()
        );
        recommendedMidpointRepository.save(recommended);
    }
}
