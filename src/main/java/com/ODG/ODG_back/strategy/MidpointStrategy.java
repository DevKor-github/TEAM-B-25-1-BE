package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;

import java.util.List;

public interface MidpointStrategy {
    List<MidpointResponseDto> calculateMidpoints(String inviteCode);
}
