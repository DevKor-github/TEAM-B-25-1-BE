package com.ODG.ODG_back.dto.place.response;

public enum PlaceSection {
    FOOD("맛집"), FUN("놀거리"), STUDY("스터디카페");
    public final String label;

    PlaceSection(String label) {
        this.label = label;
    }
}
