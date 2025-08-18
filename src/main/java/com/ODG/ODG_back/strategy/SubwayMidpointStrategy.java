package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.domain.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Component("subwayStrategy")
@RequiredArgsConstructor
public class SubwayMidpointStrategy implements MidpointStrategy{
    private final int MAX_DISTANCE = 2000; // 최대 검색 거리 (미터 단위)
    private final MeetingRepository meetingRepository;
    private final RecommendedMidpointRepository recommendedMidpointRepository;
    private final MidpointRepository midpointRepository;
    private final MidpointMapper midpointMapper;
    private final SubwayDurationTimeRepository timeRepository; // 지하철역간 이동 시간 저장소

    @Override
    public MidpointResponseDto calculateMidpoints(String inviteCode) {

        Meeting meeting = meetingRepository.findByInviteCode(inviteCode).orElseThrow(
                () -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND)
        );
        // 출발지 = 참가자 위치
        List<Participant> participants = meeting.getParticipants();
        // 목적지 = 모든 지하철역
        List<Midpoint> allMidpoints = midpointRepository.findAll();

        // 각 participant별로 allMidpoints에서 가장 가까운 지하철역을 찾아 리스트에 추가

        List<Midpoint> nearestMidpoints = new ArrayList<>();
        for (Participant participant : participants) {
            Midpoint nearest = allMidpoints.stream()
                    .min(Comparator.comparing(midpoint ->
                            participant.getLatitude().subtract(midpoint.getLatitude()).abs()
                                    .add(participant.getLongitude().subtract(midpoint.getLongitude()).abs())
                    ))
                    .orElseThrow(RuntimeException::new);
            nearestMidpoints.add(nearest);
        }

        List<MidpointScore> midpointScores = calculateMidpointScores(nearestMidpoints, allMidpoints, participants.size());

        MidpointScore best = midpointScores.stream()
                .min(Comparator.comparingDouble(MidpointScore::totalDeviation))
                .orElseThrow(RuntimeException::new);

        saveRecommendedMidpoint(best, meeting);
        return midpointMapper.toDto(best.midpoint());
    }

    // 각 참가자별 가장 가까운 역(nearbyStationsForEachParticipant)과 모든 Midpoint의 역 정보를 기반으로
    // 각 Midpoint까지의 소요 시간 합(midPointScore)을 계산하여 반환하는 메서드
    public List<MidpointScore> calculateMidpointScores(
            List<Midpoint> nearbyStationsForEachParticipant,
            List<Midpoint> allMidpoints,
            int numberOfParticipants) {

        List<MidpointScore> scores = new ArrayList<>();

        for (Midpoint midpoint : allMidpoints) {
            var score = calculateMidpointScore(nearbyStationsForEachParticipant, numberOfParticipants, midpoint);

            if(score != null) {
                scores.add(score);
            }
        }

        return scores;
    }

    public MidpointScore calculateMidpointScore(List<Midpoint> nearbyStationsForEachParticipant, int numberOfParticipants, Midpoint midpoint) {
        int totalDuration = 0;

        try{

            for (Midpoint startStation : nearbyStationsForEachParticipant) {
                // 시작역과 목적지역 이름이 모두 존재해야 함

                // SubwayDurationTimeRepository에서 소요 시간 조회
                SubwayDurationTime durationOpt = getDurationInfo(startStation, midpoint);
                totalDuration += durationOpt.getShortestDurationTime();

                return new MidpointScore(midpoint, (double) totalDuration / numberOfParticipants, totalDuration);
            }
        }
        catch (NotFoundException e) {
            // 해당 midpoint에 대한 정보가 없으면 유효하지 않은 것으로 간주
        }

        return null;
    }

    public SubwayDurationTime getDurationInfo(
            Midpoint start,
            Midpoint end) {
        // 시작역과 목적지역 이름이 모두 존재해야 함

        if(Objects.equals(start.getId(), end.getId())){
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
                java.time.LocalDateTime.now(),
                meeting,
                best.midpoint()
        );
        recommendedMidpointRepository.save(recommended);
    }

    public record MidpointScore(Midpoint midpoint, double avg, double totalDeviation) {}
}
