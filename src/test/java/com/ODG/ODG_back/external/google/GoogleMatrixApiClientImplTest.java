package com.ODG.ODG_back.external.google;

import com.ODG.ODG_back.domain.enums.TransportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleMatrixApiClientImplTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient mockWebClient;

    @Mock
    private WebClient.Builder mockBuilder;

    private GoogleMatrixApiClientImpl client;

    @BeforeEach
    void setUp() {
        when(mockBuilder.build()).thenReturn(mockWebClient);
        client = new GoogleMatrixApiClientImpl(mockBuilder, "fake-api-key");
    }

    @Test
    @DisplayName("Google API - 예상 소요 시간 행렬 반환")
    void testGetTimeMatrix() {

        // given
        List<String> origins = List.of("37.5665, 126.9780", "37.5596,126.9900");
        List<String> destinations = List.of("37.5700,126.9780", "37.5651,126.9895");

        Map<String, Object> fakeResponse = Map.of(
                "rows", List.of(
                        Map.of("elements", List.of(
                                Map.of("duration", Map.of("value", 1000)),
                                Map.of("duration", Map.of("value", 1200))
                        )),
                        Map.of("elements", List.of(
                                Map.of("duration", Map.of("value", 1300)),
                                Map.of("duration", Map.of("value", 1500))
                        ))
                )
        );

        when(mockWebClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(Map.class))
                .thenReturn(Mono.just(fakeResponse));

        // when
       int[][] matrix = client.getTimeMatrix(origins, destinations, TransportType.PUBLIC);

        // then
        assertEquals(2, matrix.length);
        assertEquals(2, matrix[0].length);
        assertEquals(1000, matrix[0][0]);
        assertEquals(1200, matrix[0][1]);
    }

}