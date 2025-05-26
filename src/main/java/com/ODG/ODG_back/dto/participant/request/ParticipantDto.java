package com.ODG.ODG_back.dto.participant.request;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ParticipantDto {
    private String name;
    private String address;
    private TransportType transport_type;

    Participant toEntity(String linkCode) {
        return null;
    }
}
