package com.ODG.ODG_back.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransportType {
    PUBLIC,
    CAR;

    @JsonCreator
    public static TransportType from(String v) {
        return TransportType.valueOf(v.trim().toUpperCase());
    }
}
