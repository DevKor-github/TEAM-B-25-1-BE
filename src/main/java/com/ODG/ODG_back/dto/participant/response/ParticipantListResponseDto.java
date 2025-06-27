package com.ODG.ODG_back.dto.participant.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ParticipantListResponseDto {
    private Long participantId;
    private String name;
    private String transportType;
    private BigDecimal lat;
    private BigDecimal lng;
}
