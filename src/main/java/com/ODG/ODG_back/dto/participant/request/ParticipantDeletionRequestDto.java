package com.ODG.ODG_back.dto.participant.request;

import com.ODG.ODG_back.domain.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;


@Getter
@AllArgsConstructor
public class ParticipantDeletionRequestDto {
    private Long participantId;
}
