package com.ODG.ODG_back.dto.midpoint.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MidpointResponseDto {
    private Long midpointId;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
