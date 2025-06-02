package com.ODG.ODG_back.external.google;

import com.ODG.ODG_back.domain.enums.TransportType;

import java.util.List;

public interface GoogleMatrixApiClient {
    int[][] getTimeMatrix(List<String> origins, List<String> destinations, TransportType transport);
}
