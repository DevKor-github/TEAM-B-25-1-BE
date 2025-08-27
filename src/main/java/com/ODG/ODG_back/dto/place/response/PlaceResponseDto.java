package com.ODG.ODG_back.dto.place.response;

import com.ODG.ODG_back.domain.enums.PlaceCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class PlaceResponseDto {

    private String placeId;
    private String name;
    private PlaceCategory category;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Integer slotNo;
    private String url;
    private boolean votedByMe = false;

    public PlaceResponseDto(String id, String name, PlaceCategory category,
            BigDecimal lat, BigDecimal lng, String address,
            int slotNo, String placeUrl) {
        this.placeId = id;
        this.name = name;
        this.category = category;
        this.latitude = lat;
        this.longitude = lng;
        this.address = address;
        this.slotNo = slotNo;
        this.url = placeUrl;
    }

}
