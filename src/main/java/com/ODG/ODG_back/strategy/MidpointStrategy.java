package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public interface MidpointStrategy {

    MidpointResponseDto calculateMidpoints(String inviteCode);

    default List<Midpoint> hubNearCenter(List<Participant> ps, List<Midpoint> stations) {
        if (stations.size() <= 25) {
            return stations;
        }

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
                        .thenComparingDouble(
                                s -> haversine(centerLat, centerLon, s.getLatitude().doubleValue(),
                                        s.getLongitude().doubleValue()))
                )
                .limit(25)
                .toList();
    }

    default double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0088; // 지구 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리 (km)
    }

    default MidpointScore scoreMidpoints(int[][] timeMatrix, List<Midpoint> midpoints,
                                         int numParticipants, List<Participant> rowOrder) {
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

    default int findRowIndex(List<Participant> rowOrder, Long pid) {
        for (int i = 0; i < rowOrder.size(); i++) {
            if (rowOrder.get(i).getId().equals(pid)) {
                return i;
            }
        }
        throw new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND);
    }

    record TimeMatrix(int[][] matrix, List<Participant> rowOrder) {};
    record MidpointScore(Midpoint midpoint, double avg, double totalDeviation,
                         int[] times) {};
}
