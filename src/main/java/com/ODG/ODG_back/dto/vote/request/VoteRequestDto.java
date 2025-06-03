package com.ODG.ODG_back.dto.vote.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteRequestDto {
    private Long placeId;
    private Long participantId;
}
