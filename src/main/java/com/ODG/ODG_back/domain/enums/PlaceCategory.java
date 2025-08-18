package com.ODG.ODG_back.domain.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlaceCategory {
    RESTAURANT("FD6"),
    ENTERTAINMENT("CT1"),
    CAFE("CE7"),
    TOURIST_ATTRACTION("AT4"),
    STUDY_CAFE("스터디카페"),
    UNKNOWN("UNKNOWN");


    private final String kakaoCode;

    public static PlaceCategory fromKakao(String code) {
        return Arrays.stream(values())
            .filter(c -> c.kakaoCode.equalsIgnoreCase(code))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
