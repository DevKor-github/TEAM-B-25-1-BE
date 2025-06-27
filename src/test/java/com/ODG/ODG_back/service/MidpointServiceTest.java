package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.strategy.MidpointStrategy;
import com.ODG.ODG_back.strategy.MidpointStrategyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MidpointServiceTest {

    @Mock
    private MidpointStrategy timeMatrixStrategy;

    @Mock
    private MidpointStrategy subwayStrategy;

    private MidpointService midpointService;

    @BeforeEach
    void setUp() {
        Map<String, MidpointStrategy> strategyMap = Map.of(
                "timeMatrixStrategy", timeMatrixStrategy,
                "subwayStrategy", subwayStrategy
        );
        midpointService = new MidpointService(strategyMap);
    }

    @Test
    @DisplayName("주어진 전략 타입에 따라 중간지점 계산")
    void testGetRecommendedMidpoint() {
        //given
        String inviteCode = "abc123";
        MidpointStrategyType strategyType = MidpointStrategyType.TIME_MATRIX;

        MidpointResponseDto dummyResponse = new MidpointResponseDto(
                1L, "광화문", BigDecimal.ONE, BigDecimal.TEN
                );

        given(timeMatrixStrategy.calculateMidpoints(inviteCode)).willReturn(dummyResponse);

        // when
        MidpointResponseDto result = midpointService.getRecommendedMidpoints(inviteCode, strategyType);

        // then
        assertNotNull(result);
        assertEquals("광화문", result.getName() );
    }
}