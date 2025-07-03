package com.ODG.ODG_back.external.google.parser;

import java.util.List;
import java.util.Map;

public class GoogleMatrixResponseParser {
    public static int[][] parseTimeMatrix(Map<String, Object> response, int originSize, int destinationSize) {
        List<Map<String, Object>> rows = (List<Map<String, Object>>) response.get("rows");
        int[][] timeMatrix = new int[originSize][destinationSize];

        for (int i = 0; i < originSize; i++) {
            Map<String, Object> row = rows.get(i);
            List<Map<String, Object>> elements = (List<Map<String, Object>>) row.get("elements");

            for (int j = 0; j < destinationSize; j++) {
                Map<String, Object> element = elements.get(j);
                Map<String, Object> duration = (Map<String, Object>) element.get("duration");

                if (duration != null) {
                    timeMatrix[i][j] = (int) duration.get("value");
                } else {
                    timeMatrix[i][j] = Integer.MAX_VALUE; // unreachable destination
                }
            }
        }
        return timeMatrix;
    }
}
