package com.ODG.ODG_back.dto.participant.response;

import com.ODG.ODG_back.domain.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ParticipantListResponseDto {

    private Long participantId;
    private String name;
    private TransportType transportType;
    private BigDecimal lat;
    private BigDecimal lng;
    private boolean hasVoted;
}
