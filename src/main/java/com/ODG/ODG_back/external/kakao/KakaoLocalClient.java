package com.ODG.ODG_back.external.kakao;

import java.time.Duration;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Component
@Slf4j
public class KakaoLocalClient {

    private final WebClient webClient;

    public KakaoLocalClient(@Value("${kakao.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
                .build();
    }

    public List<KakaoPlaceDoc> searchCategory(String categoryCode, double lat, double lng,
            int radius, int page, int size) {
        log.info(
                "[KakaoLocalClient] searchCategory code={}, lat={}, lng={}, radius={}, page={}, size={}",
                categoryCode, lat, lng, radius, page, size);

        KakaoResp resp = webClient.get()
                .uri(uri -> uri.path("/v2/local/search/category.json")
                        .queryParam("category_group_code", categoryCode)
                        .queryParam("x", lng) // longitude
                        .queryParam("y", lat) // latitude
                        .queryParam("radius", radius)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sort", "distance")
                        .build())
                .retrieve()
                .bodyToMono(KakaoResp.class)
                .retryWhen(Retry.backoff(1, Duration.ofMillis(300))
                        .filter(ex -> ex instanceof WebClientResponseException.TooManyRequests))
                .block();

        int count = (resp != null && resp.documents != null) ? resp.documents.size() : 0;
        log.info("[KakaoLocalClient] searchCategory result count={}", count);

        return resp != null && resp.documents != null ? resp.documents : List.of();
    }

    public List<KakaoPlaceDoc> searchKeyword(String query, double lat, double lng, int radius,
            int page, int size) {
        log.info(
                "[KakaoLocalClient] searchKeyword query={}, lat={}, lng={}, radius={}, page={}, size={}",
                query, lat, lng, radius, page, size);

        KakaoResp resp = webClient.get()
                .uri(uri -> uri.path("/v2/local/search/category.json")
                        .queryParam("query", query)
                        .queryParam("x", lng) // longitude
                        .queryParam("y", lat) // latitude
                        .queryParam("radius", radius)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sort", "distance")
                        .build())
                .retrieve()
                .bodyToMono(KakaoResp.class)
                .retryWhen(Retry.backoff(1, Duration.ofMillis(300))
                        .filter(ex -> ex instanceof WebClientResponseException.TooManyRequests))
                .block();

        int count = (resp != null && resp.documents != null) ? resp.documents.size() : 0;
        log.info("[KakaoLocalClient] searchKeyword result count={}", count);

        return resp != null && resp.documents != null ? resp.documents : List.of();
    }

    // ===== Kakao Response DTO =====
    @Data
    public static class KakaoResp {

        public List<KakaoPlaceDoc> documents;
    }

    @Data
    public static class KakaoPlaceDoc {

        public String id;
        public String place_name;
        public String category_group_code;
        public String address_name;
        public String road_address_name;
        public double x; // longitude
        public double y; // latitude
        public String distance;
        public String place_url;

    }

}
