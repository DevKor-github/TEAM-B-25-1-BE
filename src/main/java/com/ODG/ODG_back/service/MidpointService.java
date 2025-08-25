package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.ParticipantRepository;
import com.ODG.ODG_back.strategy.MidpointStrategy;
import com.ODG.ODG_back.strategy.MidpointStrategyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MidpointService {

    // 전략 빈 자동 주입
    private final Map<String, MidpointStrategy> strategyMap;
    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;

    public MidpointResponseDto getRecommendedMidpoints(String inviteCode, MidpointStrategyType strategyType, String userId) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        participantRepository.findByUserIdAndMeeting(userId, meeting)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));
        MidpointStrategy strategy = strategyMap.get(strategyType.getBeanName());

        return strategy.calculateMidpoints(inviteCode);
    }
}
