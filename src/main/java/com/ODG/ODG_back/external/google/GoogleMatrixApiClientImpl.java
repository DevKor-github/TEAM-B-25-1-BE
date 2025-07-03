package com.ODG.ODG_back.external.google;

import com.ODG.ODG_back.domain.enums.TransportType;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;
import java.util.Map;

import static com.ODG.ODG_back.external.google.parser.GoogleMatrixResponseParser.parseTimeMatrix;

@Component("googleMatrixApiClient")
public class GoogleMatrixApiClientImpl implements GoogleMatrixApiClient {

    private final String googleApiKey;
    private final WebClient webClient;

    public GoogleMatrixApiClientImpl(WebClient.Builder builder, @Value("${google.api-key}") String googleApiKey) {
        this.webClient = builder.build();
        this.googleApiKey = googleApiKey;
    }

    @Override
    public int[][] getTimeMatrix(List<String> origins, List<String> destinations, TransportType transport) {

        String url = buildUrl(origins, destinations, transport);
        Map<String, Object> response = fetchApiResponse(url);

        String status = (String) response.get("status");
        if (!"OK".equals(status)) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_RESPONSE_ERROR, status);
        }

        return parseTimeMatrix(response, origins.size(), destinations.size());
    }

    private String buildUrl(List<String> origins, List<String> destinations, TransportType transport) {

        String originsParam = String.join("|", origins);
        String destinationsParam = String.join("|", destinations);
        String mode = transport == TransportType.CAR ? "driving" : "transit";

        String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                        "origins=%s&destinations=%s&mode=%s&key=%s",
                originsParam, destinationsParam, mode, googleApiKey
        );

        return url;
    }

    private Map<String, Object> fetchApiResponse(String url) {
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_CONNECTION_FAILED, e.getMessage());
        }
    }
}
