package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.external.google.GoogleMatrixApiClient;
import com.ODG.ODG_back.mapper.MidpointMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.MidpointRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.ODG.ODG_back.domain.enums.TransportType.CAR;
import static com.ODG.ODG_back.domain.enums.TransportType.PUBLIC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


class TimeMatrixMidpointStrategyTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MidpointRepository midpointRepository;

    @Mock
    private RecommendedMidpointRepository recommendedMidpointRepository;

    @Mock
    private GoogleMatrixApiClient matrixApiClient;

    @Mock
    private MidpointMapper midpointMapper;

    @InjectMocks
    private TimeMatrixMidpointStrategy strategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("중간지점 정확히 계산하기")
    void calculateMidpoint() {
        // given
        // 모임 정보 설정
        Meeting meeting = new Meeting();
        meeting.setInviteCode("abc123");
        when(meetingRepository.findByInviteCode("abc123")).thenReturn(meeting);

        // 참가자 추가
        Participant p1 = Participant.builder()
                .name("민재")
                .transport(PUBLIC)
                .build();
        Participant p2 = Participant.builder()
                .name("승민")
                .transport(CAR)
                .build();
        Participant p3 = Participant.builder()
                .name("준우")
                .transport(PUBLIC)
                .build();
        meeting.setParticipants(List.of(p1, p2, p3));

        // 후보 중간지점 (Midpoint) 설정
        Midpoint m1 = Midpoint.builder()
                .name("강남역")
                .build();
        Midpoint m2 = Midpoint.builder()
                .name("홍대입구")
                .build();
        Midpoint m3 = Midpoint.builder()
                .name("서울역")
                .build();
        when(midpointRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        // 모의 Google API 응답
        when(matrixApiClient.getTimeMatrix(any(), any(), eq(PUBLIC))).thenReturn(
                new int[][] {
                        {30, 20, 27}, // p1 to m1, m2, m3
                        {35, 22, 23} // p3 to m1, m2, m3
                }
        );

        when(matrixApiClient.getTimeMatrix(any(), any(), eq(CAR))).thenReturn(
                new int[][] {
                        {15, 18, 10} // p2 to m1, m2, m3
                }
        );

        when(midpointMapper.toDto(any())).thenAnswer(invocation ->
        {
            Midpoint mp = invocation.getArgument(0);
            return new MidpointResponseDto(mp.getId(), mp.getName(), mp.getLatitude(), mp.getLongitude());
        });

        // when
        MidpointResponseDto result = strategy.calculateMidpoints("abc123");

        //then
        assertNotNull(result);
        assertEquals("홍대입구", result.getName());
    }
}