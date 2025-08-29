package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.mapper.MidpointMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
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

    @Mock
    private RecommendedMidpointRepository recommendedMidpointRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MidpointMapper midpointMapper;
    private MidpointService midpointService;

    @BeforeEach
    void setUp() {
        Map<String, MidpointStrategy> strategyMap = Map.of(
                "timeMatrixStrategy", timeMatrixStrategy,
                "subwayStrategy", subwayStrategy
        );
        midpointService = new MidpointService(strategyMap, recommendedMidpointRepository, meetingRepository, midpointMapper);
    }

    @Test
    @DisplayName("주어진 전략 타입에 따라 중간지점 계산")
    void testGetRecommendedMidpoint() {
        //given
        String inviteCode = "abc123";
        MidpointStrategyType strategyType = MidpointStrategyType.TIME_MATRIX;
        Meeting meeting = new Meeting();
        Midpoint midpoint = new Midpoint(
                1L,
                "광화문",
                new BigDecimal("37.5713"),
                new BigDecimal("126.9768"),
                "02-123-4567",
                10.0
        );
        int[] times = new int[]{10, 10, 10};

        MidpointStrategy.MidpointScore dummyScore = new MidpointStrategy.MidpointScore(
                midpoint,10,10,times
        );

        MidpointStrategy.MidpointScoreWithMeta dummyResponse = new MidpointStrategy.MidpointScoreWithMeta(
                dummyScore,
                1L,
                10,
                3
        );

        given(timeMatrixStrategy.calculateMidpoints(meeting)).willReturn(dummyResponse);

        // when
        MidpointResponseDto result = midpointService.getRecommendedMidpoints(inviteCode,
                strategyType);

        // then
        assertNotNull(result);
        assertEquals("광화문", result.getName());
    }
}