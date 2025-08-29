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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@Component("timeMatrixStrategy")
@RequiredArgsConstructor
@Slf4j
public class TimeMatrixMidpointStrategy implements MidpointStrategy {

    private final MeetingRepository meetingRepository;
    private final GoogleMatrixApiClient matrixApiClient; // 소요 시간 API 요청
    private final MidpointRepository midpointRepository;
    private final MidpointMapper midpointMapper;

    private final AuthService authService;

    @Override
    public MidpointScoreWithMeta calculateMidpoints(Meeting meeting) {
        log.info("Calculating midpoints for inviteCode: {}", meeting.getInviteCode());

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

        return new MidpointScoreWithMeta(
                best,
                currentPid,
                selfTime,
                participants.size()
        );
    }

    private TimeMatrix buildTimeMatrix(List<Participant> participants, List<Midpoint> candidates) {

        List<String> destinations = candidates.stream()
                .map(m -> m.getLatitude() + "," + m.getLongitude())
                .toList();
        int[][] timeMatrix = new int[participants.size()][destinations.size()];

        // transportType 기준으로 그룹화하되, 키 순서를 안정화(TreeMap)하고, 각 그룹 내부는 id 기준으로 정렬
        Map<TransportType, List<Participant>> grouped = participants.stream()
                .collect(groupingBy(Participant::getTransportType, TreeMap::new,
                        java.util.stream.Collectors.toList()));
        List<Participant> rowOrder = new ArrayList<>(participants.size());

        int rowIndex = 0;

        for (var entry : grouped.entrySet()) {
            List<Participant> group = entry.getValue().stream()
                    .sorted(Comparator.comparing(Participant::getId))
                    .toList();

            List<String> origins = group.stream()
                    .map(p -> p.getLatitude() + "," + p.getLongitude())
                    .toList();

            int[][] groupMatrix = matrixApiClient.getTimeMatrix(origins, destinations,
                    entry.getKey());

            for (int i = 0; i < group.size(); i++) {
                timeMatrix[rowIndex] = groupMatrix[i];
                rowOrder.add(group.get(i)); // 행과 참가자 매핑 기록
                rowIndex++;
            }
        }
        return new TimeMatrix(timeMatrix, rowOrder);
    }

}
