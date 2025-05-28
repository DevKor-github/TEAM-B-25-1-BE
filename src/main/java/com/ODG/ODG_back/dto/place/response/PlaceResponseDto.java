package com.ODG.ODG_back.dto.place.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PlaceResponseDto {
    private Long placeId;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
