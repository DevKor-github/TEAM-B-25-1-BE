package com.ODG.ODG_back.dto.participant.request;

import com.ODG.ODG_back.domain.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public class ParticipantDto {
    private Long id;
    private String name;
    private String address;

    private TransportType transport_type;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
