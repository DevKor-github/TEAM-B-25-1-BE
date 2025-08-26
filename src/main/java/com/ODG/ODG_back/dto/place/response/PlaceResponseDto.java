package com.ODG.ODG_back.dto.place.response;

import com.ODG.ODG_back.domain.enums.PlaceCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PlaceResponseDto {

    private String placeId;
    private String name;
    private PlaceCategory category;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Integer slotNo;
    private String url;
}
