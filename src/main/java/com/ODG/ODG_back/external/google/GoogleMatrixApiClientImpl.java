package com.ODG.ODG_back.external.google;

import com.ODG.ODG_back.domain.enums.TransportType;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;
import java.util.Map;

import static com.ODG.ODG_back.external.google.parser.GoogleMatrixResponseParser.parseTimeMatrix;

@Component("googleMatrixApiClient")
@Slf4j
public class GoogleMatrixApiClientImpl implements GoogleMatrixApiClient {

    private final String googleApiKey;
    private final WebClient webClient;

    public GoogleMatrixApiClientImpl(WebClient.Builder builder, @Value("${google.api-key}") String googleApiKey) {
        this.webClient = builder.build();
        this.googleApiKey = googleApiKey;
    }

    @Override
    public int[][] getTimeMatrix(List<String> origins, List<String> destinations, TransportType transport) {

        int numOrigins = origins.size();
        int numDestinations = destinations.size();
        log.info("getTimeMatrix: origins={}, destinations={}, transport={}", numOrigins, numDestinations, transport);

        int maxElements = 100;
        if (numOrigins * numDestinations > maxElements) {
            int maxDestPerBatch = Math.max(1, maxElements / numOrigins);
            // Split destinations into chunks of maxDestPerBatch
            int numChunks = (numDestinations + maxDestPerBatch - 1) / maxDestPerBatch;
            log.info("Batching Distance Matrix API requests: {} chunks, chunk size up to {}", numChunks, maxDestPerBatch);

            int[][] resultMatrix = new int[numOrigins][numDestinations];
            int destStart = 0;
            int chunkIdx = 0;
            while (destStart < numDestinations) {
                int destEnd = Math.min(destStart + maxDestPerBatch, numDestinations);
                List<String> destChunk = destinations.subList(destStart, destEnd);
                String url = buildUrl(origins, destChunk, transport);
                int elementsCount = numOrigins * destChunk.size();
                log.info("Requesting chunk {}: elements={}, destinations {}-{}", chunkIdx + 1, elementsCount, destStart, destEnd - 1);
                Map<String, Object> response = fetchApiResponse(url);
                log.info("Received response from Distance Matrix API: {}", response);

                String status = (String) response.get("status");
                log.info("Chunk {} response status: {}", chunkIdx + 1, status);
                if (!"OK".equals(status)) {
                    throw new ExternalApiException(ErrorCode.EXTERNAL_API_RESPONSE_ERROR, status);
                }
                int[][] chunkMatrix = parseTimeMatrix(response, numOrigins, destChunk.size());
                // Copy chunkMatrix into resultMatrix
                for (int i = 0; i < numOrigins; i++) {
                    System.arraycopy(chunkMatrix[i], 0, resultMatrix[i], destStart, destChunk.size());
                }
                destStart = destEnd;
                chunkIdx++;
            }
            return resultMatrix;
        } else {
            String url = buildUrl(origins, destinations, transport);
            int elementsCount = numOrigins * numDestinations;
            log.info("Requesting single Distance Matrix API: elements={}", elementsCount);
            Map<String, Object> response = fetchApiResponse(url);
            log.info("Received response from Distance Matrix API: {}", response);
            String status = (String) response.get("status");
            log.info("Single response status: {}", status);
            if (!"OK".equals(status)) {
                throw new ExternalApiException(ErrorCode.EXTERNAL_API_RESPONSE_ERROR, status);
            }
            return parseTimeMatrix(response, numOrigins, numDestinations);
        }
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
            log.error("Error fetching Distance Matrix API response", e);
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_CONNECTION_FAILED, e.getMessage());
        }
    }
}
