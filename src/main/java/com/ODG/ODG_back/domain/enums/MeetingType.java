package com.ODG.ODG_back.domain.enums;

import lombok.Getter;

import java.util.List;
import static com.ODG.ODG_back.domain.enums.PlaceCategory.*;

@Getter
public enum MeetingType {
    SOCIAL(List.of(RESTAURANT, CAFE, ENTERTAINMENT)),
    PROJECT(List.of(STUDY_CAFE));

    private final List<PlaceCategory> categories;

    MeetingType(List<PlaceCategory> categories) {
        this.categories = categories;
    }
}
