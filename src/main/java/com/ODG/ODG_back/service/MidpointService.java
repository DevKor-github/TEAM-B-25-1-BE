package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.mapper.MidpointMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import com.ODG.ODG_back.strategy.MidpointStrategy;
import com.ODG.ODG_back.strategy.MidpointStrategyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidpointService {

    // 전략 빈 자동 주입
    private final Map<String, MidpointStrategy> strategyMap;
    private final RecommendedMidpointRepository recommendedMidpointRepository;
    private final MeetingRepository meetingRepository;
    private final MidpointMapper midpointMapper;

    public MidpointResponseDto getRecommendedMidpoints(String inviteCode,
            MidpointStrategyType strategyType) {
        MidpointStrategy strategy = strategyMap.get(strategyType.getBeanName());
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode).orElseThrow(
                () -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND)
        );

        MidpointStrategy.MidpointScoreWithMeta best = strategy.calculateMidpoints(meeting);
        saveRecommendedMidpoint(best, meeting);

        return midpointMapper.toDtoSelfOnly(
                best.midpointScore().midpoint(),
                best.midpointScore().avg(),
                best.midpointScore().totalDeviation(),
                best.participantId(),
                best.selfTimeSeconds(),
                best.participantCount()
        );
    }

    private void saveRecommendedMidpoint(MidpointStrategy.MidpointScoreWithMeta best, Meeting meeting) {
        log.info("Saving recommended midpoint: {}, average time: {}", best.midpointScore().midpoint().getName(),
                best.midpointScore().avg());
        RecommendedMidpoint recommended = new RecommendedMidpoint(
                null,
                best.midpointScore().avg(),
                1,
                java.time.LocalDateTime.now(),
                meeting,
                best.midpointScore().midpoint()
        );
        recommendedMidpointRepository.save(recommended);
    }
}
