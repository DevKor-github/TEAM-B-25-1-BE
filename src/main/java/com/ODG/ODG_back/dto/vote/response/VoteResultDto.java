package com.ODG.ODG_back.dto.vote.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteResultDto {
    private Long placeId;
    private String name;
    private int voteCount;
}
