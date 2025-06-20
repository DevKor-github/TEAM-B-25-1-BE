package com.ODG.ODG_back.dto.participant.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ParticipantListResponseDto {
    private Long participantId;
    private String name;
    private String transportType;
    private Double latitude;
    private Double longitude;
}
