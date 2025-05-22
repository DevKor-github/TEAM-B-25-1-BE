package com.ODG.ODG_back.external.google;

import java.math.BigDecimal;
import java.util.List;

public interface GoogleMatrixApiClient {
    int[][] getTimeMatrix(List<List<BigDecimal>> origins, List<List<BigDecimal>> destinations);
}
