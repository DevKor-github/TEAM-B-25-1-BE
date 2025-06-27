package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.external.google.GoogleMatrixApiClient;
import com.ODG.ODG_back.mapper.MidpointMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.MidpointRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
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

    @Override
    public MidpointResponseDto calculateMidpoints(String inviteCode) {

        Meeting meeting = meetingRepository.findByInviteCode(inviteCode).orElseThrow(
                () -> new IllegalArgumentException("Meeting not found.")
        );
        List<Participant> participants = meeting.getParticipants();
        List<Midpoint> allMidpoints = midpointRepository.findAll();

        int[][] timeMatrix = buildTimeMatrix(participants, allMidpoints);
        MidpointScore best = scoreMidpoints(timeMatrix, allMidpoints, participants.size());

        saveRecommendedMidpoint(best, meeting);
        return midpointMapper.toDto(best.midpoint());
    }

    private int[][] buildTimeMatrix(List<Participant> participants, List<Midpoint> allMidpoints) {

        List <String> destinations = allMidpoints.stream()
                .map(m -> m.getLatitude() + ", " + m.getLongitude())
                .toList();
        int[][] timeMatrix = new int[participants.size()][destinations.size()];

        var grouped = participants.stream()
                .collect(groupingBy(Participant::getTransportType));
        int rowIndex = 0;

        for (var entry: grouped.entrySet()) {
            List<Participant> group = entry.getValue();
            List<String> origins = group.stream()
                    .map(p -> p.getLatitude() + ", " + p.getLongitude())
                    .toList();

            int[][] groupMatrix = matrixApiClient.getTimeMatrix(origins, destinations, entry.getKey());

            for (int i = 0; i < group.size(); i++) {
                timeMatrix[rowIndex++] = groupMatrix[i];
            }
        }
        return timeMatrix;
    }

    private MidpointScore scoreMidpoints(int[][] timeMatrix, List<Midpoint> midpoints, int numParticipants) {
        List<MidpointScore> scored = new ArrayList<>();

        for (int j = 0; j < midpoints.size(); j++) {
            int sum = 0;
            for (int i = 0; i < numParticipants; i++) {
                sum += timeMatrix[i][j];
            }
            double avg = sum / (double) numParticipants;

            double totalDeviation = 0;
            for (int i = 0; i < numParticipants; i++) {
                totalDeviation += Math.abs(timeMatrix[i][j] - avg);
            }

            scored.add(new MidpointScore(midpoints.get(j), avg, totalDeviation ));
        }

        return scored.stream()
                .sorted(Comparator.comparingDouble(MidpointScore::avg)
                        .thenComparingDouble(MidpointScore::totalDeviation))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No MIDPOINT found"));
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

    private record MidpointScore(Midpoint midpoint, double avg, double totalDeviation) {}
}
