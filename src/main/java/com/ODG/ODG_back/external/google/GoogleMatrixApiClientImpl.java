package com.ODG.ODG_back.external.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Component("googleMatrixApiClient")
public class GoogleMatrixApiClientImpl implements GoogleMatrixApiClient {

    @Value("{google.api.key}")
    private String googleApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public int[][] getTimeMatrix(List<List<BigDecimal>> origins, List<List<BigDecimal>> destinations) {
        return new int[0][];
    }
}
