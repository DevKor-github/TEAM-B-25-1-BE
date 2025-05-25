package com.ODG.ODG_back.dto.participant.request;

import com.ODG.ODG_back.domain.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;


@Getter
@AllArgsConstructor
public class ParticipantModificationRequestDto {
    private BigInteger participantId;
    private String name;
    private TransportType transport_type;
    private String address;
}
