package com.ODG.ODG_back.dto.participant.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class ParticipantRegisterResponseDto {

    private Long participantId;
    private String participantName;
    private String accessToken;
    private long expiresIn;
}
