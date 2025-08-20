package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import com.ODG.ODG_back.domain.enums.TransportType;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.external.google.GoogleMatrixApiClient;
import com.ODG.ODG_back.mapper.MidpointMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.MidpointRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import com.ODG.ODG_back.service.AuthService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@Component("timeMatrixStrategy")
@RequiredArgsConstructor
public class TimeMatrixMidpointStrategy implements MidpointStrategy {

    private final MeetingRepository meetingRepository;
    private final RecommendedMidpointRepository recommendedMidpointRepository;
    private final GoogleMatrixApiClient matrixApiClient; // 소요 시간 API 요청
    private final MidpointRepository midpointRepository;
    private final MidpointMapper midpointMapper;

    private final AuthService authService;

    @Override
    public MidpointResponseDto calculateMidpoints(String inviteCode) {

        Meeting meeting = meetingRepository.findByInviteCode(inviteCode).orElseThrow(
                () -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND)
        );
        // 출발지 = 참가자 위치
        List<Participant> participants = meeting.getParticipants();
        // 목적지 = 모든 지하철역
        List<Midpoint> allMidpoints = midpointRepository.findAll();

        List<Midpoint> candidates = hubNearCenter(participants, allMidpoints);

        TimeMatrix tm = buildTimeMatrix(participants, candidates);
        int[][] timeMatrix = tm.matrix();
        List<Participant> rowOrder = tm.rowOrder();

        MidpointScore best = scoreMidpoints(timeMatrix, candidates, participants.size(), rowOrder);

        Long currentPid = authService.getCurrentParticipantId(meeting);
        int rowIndex = findRowIndex(rowOrder, currentPid);
        int selfTime = best.times()[rowIndex];

        saveRecommendedMidpoint(best, meeting);
        return midpointMapper.toDtoSelfOnly(
            best.midpoint(),
            best.avg(),
            best.totalDeviation(),
            currentPid,
            selfTime
        );
    }

    private List<Midpoint> hubNearCenter(List<Participant> ps, List<Midpoint> stations) {
        if (stations.size() <= 25) return stations;

        double centerLat = ps.stream()
            .map(Participant::getLatitude)
            .mapToDouble(BigDecimal::doubleValue)
            .average()
            .orElse(0.0);
        double centerLon = ps.stream()
            .map(Participant::getLongitude)
            .mapToDouble(BigDecimal::doubleValue)
            .average()
            .orElse(0.0);

        ToDoubleFunction<Midpoint> hub = s -> {
            try {
                Double v = s.getHubScore();
                return v == null ? 0.0 : v;
            } catch (Exception e) {
                return 0.0;
            }
        };

        return stations.stream()
            .sorted(Comparator.comparingDouble((Midpoint s) ->
        -hub.applyAsDouble(s))
                .thenComparingDouble(s -> haversine(centerLat, centerLon, s.getLatitude().doubleValue(), s.getLongitude().doubleValue()))
            )
            .limit(25)
            .toList();
    }

    private record TimeMatrix(int[][] matrix, List<Participant> rowOrder) {}

    private TimeMatrix buildTimeMatrix(List<Participant> participants, List<Midpoint> allMidpoints) {

        List <String> destinations = allMidpoints.stream()
                .map(m -> m.getLatitude() + ", " + m.getLongitude())
                .toList();
        int[][] timeMatrix = new int[participants.size()][destinations.size()];

        // transportType 기준으로 그룹화하되, 키 순서를 안정화(TreeMap)하고, 각 그룹 내부는 id 기준으로 정렬
        Map<TransportType, List<Participant>> grouped = participants.stream()
            .collect(groupingBy(Participant::getTransportType, TreeMap::new, java.util.stream.Collectors.toList()));
        List<Participant> rowOrder = new ArrayList<>(participants.size());

        int rowIndex = 0;

        for (var entry : grouped.entrySet()) {
            List<Participant> group = entry.getValue().stream()
                .sorted(Comparator.comparing(Participant::getId))
                .toList();

            List<String> origins = group.stream()
                    .map(p -> p.getLatitude() + ", " + p.getLongitude())
                    .toList();

            int[][] groupMatrix = matrixApiClient.getTimeMatrix(origins, destinations, entry.getKey());

            for (int i = 0; i < group.size(); i++) {
                timeMatrix[rowIndex] = groupMatrix[i];
                rowOrder.add(group.get(i)); // 행과 참가자 매핑 기록
                rowIndex++;
            }
        }
        return new TimeMatrix(timeMatrix, rowOrder);
    }

    private MidpointScore scoreMidpoints(int[][] timeMatrix, List<Midpoint> midpoints, int numParticipants, List<Participant> rowOrder) {
        List<MidpointScore> scored = new ArrayList<>();

        for (int j = 0; j < midpoints.size(); j++) {
            int sum = 0;
            int[] times = new int[numParticipants];
            for (int i = 0; i < numParticipants; i++) {
                times[i] = timeMatrix[i][j];
                sum += times[i];
            }
            double avg = sum / (double) numParticipants;

            double totalDeviation = 0;
            for (int i = 0; i < numParticipants; i++) {
                totalDeviation += Math.abs(times[i] - avg);
            }

            scored.add(new MidpointScore(midpoints.get(j), avg, totalDeviation, times));
        }

        return scored.stream()
                .sorted(Comparator.comparingDouble(MidpointScore::avg)
                        .thenComparingDouble(MidpointScore::totalDeviation))
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }

    private int findRowIndex(List<Participant> rowOrder, Long pid) {
        for (int i = 0; i < rowOrder.size(); i++) {
            if (rowOrder.get(i).getId().equals(pid)) return i;
        }
        throw new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND);
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0088; // 지구 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리 (km)
    }

    private void saveRecommendedMidpoint(MidpointScore best, Meeting meeting) {
        RecommendedMidpoint recommended = new RecommendedMidpoint(
                null,
                best.avg(),
                1,
                java.time.LocalDateTime.now(),
                meeting,
                best.midpoint()
        );
        recommendedMidpointRepository.save(recommended);
    }

    private record MidpointScore(Midpoint midpoint, double avg, double totalDeviation, int[] times) {}
}
