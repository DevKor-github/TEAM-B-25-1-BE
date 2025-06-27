package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("subwayStrategy")
@RequiredArgsConstructor
public class SubwayMidpointStrategy implements MidpointStrategy{

    @Override
    public MidpointResponseDto calculateMidpoints(String inviteCode) {
        return new MidpointResponseDto(1L, "dummy", BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
