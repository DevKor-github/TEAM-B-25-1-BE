package com.ODG.ODG_back.strategy;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.SubwayDurationTime;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.mapper.MidpointMapper;
import com.ODG.ODG_back.repository.*;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.ODG.ODG_back.domain.enums.TransportType.CAR;
import static com.ODG.ODG_back.domain.enums.TransportType.PUBLIC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@SpringBootTest
class SubwayMidpointStrategyTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private MidpointRepository midpointRepository;

    @Mock
    private SubwayDurationTimeRepository timeRepository;

    @Mock
    private RecommendedMidpointRepository recommendedMidpointRepository;

    @Mock
    private MidpointMapper midpointMapper;

    @InjectMocks
    private SubwayMidpointStrategy strategy;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getDurationInfo DB 연동 통합 테스트")
    void getDurationInfoIntegrationTest() {
        // given

        Midpoint m1 = Midpoint.builder().name("강남").id(1002000222L).build();
        Midpoint m2 = Midpoint.builder().name("역삼").id(1077000687L).build();

        // 실제 DB에 해당 데이터가 있어야 함
        SubwayDurationTime time = timeRepository.findByStartAndEnd(m1.getId(), m2.getId())
                .orElseThrow(() -> new RuntimeException("DB에 데이터가 없습니다."));

        System.out.println(time);
        assert time != null;
    }

    @Test
    @DisplayName("calculateMidpointScore 단위 테스트")
    void calculateMidpointScoreTest() {

        Midpoint m1 = Midpoint.builder().name("강남").id(1002000222L).build();
        Midpoint m2 = Midpoint.builder().name("역삼").id(1077000687L).build();

        List<Midpoint> participants = List.of(m1, m2);

        // 실제 DB에 m1, m2 간 데이터가 있어야 함
        Midpoint resultMidpoint = m2;
        var score = strategy.calculateMidpointScore(participants, participants.size(),
                resultMidpoint);

        assertNotNull(score);
        System.out.println("Calculated Score: " + score);
        assertEquals(7.0, score.totalDeviation(), "Score should be 7.0");
//            assertTrue(score., "Score should be greater than 0");
    }

    @Test
    @DisplayName("calculateMidpointScores 단위 테스트")
    void calculateMidpointScoresTest() {
        Midpoint m1 = Midpoint.builder().name("강남").id(1002000222L).build();
        Midpoint m2 = Midpoint.builder().name("역삼").id(1077000687L).build();

        List<Midpoint> participants = List.of(m1, m2);
        List<Midpoint> allMidpoints = List.of(m1, m2);

        var scores = strategy.calculateMidpointScores(participants, allMidpoints,
                participants.size());

        assertNotNull(scores);
        assertFalse(scores.isEmpty());
    }

    @Test
    @DisplayName("getDurationInfo 예외 테스트")
    void getDurationInfoExceptionTest() {
        Midpoint m1 = Midpoint.builder().name("없는역1").id(9999999999L).build();
        Midpoint m2 = Midpoint.builder().name("없는역2").id(8888888888L).build();

        assertThrows(RuntimeException.class, () -> {
            strategy.getDurationInfo(m1, m2);
        });
    }

    @Test
    @DisplayName("getMidPoint 정상 동작 테스트")
    void getMidpointTest() {
        Meeting meeting = new Meeting();
        meeting.setInviteCode("abc123");

        // 참가자 추가
        Participant p1 = Participant.builder()
                .name("민재")
                .transportType(PUBLIC)
                .latitude(BigDecimal.valueOf(127.037))
                .longitude(BigDecimal.valueOf(33.497)) //역삼 근처
                .build();
        Participant p2 = Participant.builder()
                .name("승민")
                .transportType(PUBLIC)
                .latitude(BigDecimal.valueOf(127.029))
                .longitude(BigDecimal.valueOf(33.497)) //강남 근처
                .build();

        meeting.setParticipants(List.of(p1, p2));

        when(meetingRepository.findByInviteCode("abc123")).thenReturn(Optional.of(meeting));

        // 후보 중간지점 (Midpoint) 설정
        Midpoint yeoksam = Midpoint.builder()
                .id(1002000222L)
                .latitude(BigDecimal.valueOf(127.037))
                .longitude(BigDecimal.valueOf(33.497))
                .name("역삼")
                .build();

        Midpoint gangnam = Midpoint.builder()
                .id(1077000687L)
                .name("강남")
                .latitude(BigDecimal.valueOf(127.029))
                .longitude(BigDecimal.valueOf(33.497))
                .build();

        when(midpointRepository.findAll()).thenReturn(List.of(yeoksam, gangnam));

        // 지하철 시간 정보 설정
        SubwayDurationTime gToYTime = new SubwayDurationTime()
                .builder()
                .start(gangnam.getId())
                .end(yeoksam.getId())
                .shortestDurationTime(5) // 예시 소요 시간
                .build();
        SubwayDurationTime yToGTime = new SubwayDurationTime()
                .builder()
                .start(yeoksam.getId())
                .end(gangnam.getId())
                .shortestDurationTime(10) // 예시 소요 시간
                .build();

        SubwayDurationTime samePlaceTime = new SubwayDurationTime()
                .builder()
                .start(yeoksam.getId())
                .end(yeoksam.getId())
                .shortestDurationTime(0) // 예시 소요 시간
                .build();

        when(timeRepository.findByStartAndEnd(yeoksam.getId(), yeoksam.getId()))
                .thenReturn(Optional.of(samePlaceTime)); // 예시 소요 시간

        when(timeRepository.findByStartAndEnd(gangnam.getId(), gangnam.getId()))
                .thenReturn(Optional.of(samePlaceTime)); // 예시 소요 시간

        when(timeRepository.findByStartAndEnd(yeoksam.getId(), gangnam.getId()))
                .thenReturn(Optional.of(yToGTime)); // 예시 소요 시간

        when(timeRepository.findByStartAndEnd(gangnam.getId(), yeoksam.getId()))
                .thenReturn(Optional.of(gToYTime)); // 예시 소요 시간

        when(midpointMapper.toDto(any())).thenAnswer(invocation ->
        {
            Midpoint mp = invocation.getArgument(0);
            return new MidpointResponseDto(mp.getId(), mp.getName(), mp.getLatitude(),
                    mp.getLongitude());
        });

        MidpointResponseDto result = strategy.calculateMidpoints("abc123");
        assertEquals("역삼", result.getName());
    }
}
