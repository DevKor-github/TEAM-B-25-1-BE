package com.ODG.ODG_back.dto.participant.response;

import com.ODG.ODG_back.domain.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class ParticipantResponseDto {
    private BigInteger participantId;
    private String name;
    private TransportType transport_type;

    private BigDecimal lat;
    private BigDecimal lng;
    private String address;

}
