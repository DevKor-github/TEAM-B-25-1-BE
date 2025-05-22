package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.external.google.GoogleMatrixApiClient;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("timeMatrixStrategy")
@RequiredArgsConstructor
public class TimeMatrixMidpointStrategy implements MidpointStrategy {

    private final MeetingRepository meetingRepository;
    private final RecommendedMidpointRepository recommendedMidpointRepository;
    private final GoogleMatrixApiClient matrixApiClient; // 소요 시간 API 요청

    @Override
    public List<MidpointResponseDto> calculateMidpoints(String inviteCode) {
        return List.of();
    }
}
