package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.strategy.MidpointStrategy;
import com.ODG.ODG_back.strategy.MidpointStrategyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MidpointService {

    // 전략 빈 자동 주입
    private final Map<String, MidpointStrategy> strategyMap;

    public List<MidpointResponseDto> getRecommendedMidpoints(String inviteCode, MidpointStrategyType strategyType) {
        MidpointStrategy strategy = strategyMap.get(strategyType.getBeanName());

        return strategy.calculateMidpoints(inviteCode);
    }
}
