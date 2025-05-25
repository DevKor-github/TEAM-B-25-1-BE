package com.ODG.ODG_back.dto.vote.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteRequestDTO {
    private Long placeId;
    private Long participantId;
}
