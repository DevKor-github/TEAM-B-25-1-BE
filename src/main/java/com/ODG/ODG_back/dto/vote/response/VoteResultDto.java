package com.ODG.ODG_back.dto.vote.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteResultDto {

    private int slotNo;
    private Long voteCount;
}
