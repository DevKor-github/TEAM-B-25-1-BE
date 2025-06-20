package com.ODG.ODG_back.dto.participant.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantListResponseDto {
    private Long participantId;
    private String name;
    private String transportType;
    private Double lat;
    private Double lng;
}
