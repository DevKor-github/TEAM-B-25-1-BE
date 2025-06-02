package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;

public interface MidpointStrategy {
    MidpointResponseDto calculateMidpoints(String inviteCode);
}
