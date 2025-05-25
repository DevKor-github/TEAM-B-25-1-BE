package com.ODG.ODG_back.dto.participant.request;

import com.ODG.ODG_back.domain.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ParticipantAdministrationRequestDto {
    private String name;
    private String address;
    private TransportType transport_type;


}
