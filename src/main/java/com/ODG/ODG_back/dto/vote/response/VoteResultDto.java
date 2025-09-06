package com.ODG.ODG_back.dto.vote.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class VoteResultDto {

    @Getter
    private int slotNo;
    private Long voteCount;

    public int getVoteCount() {
        return voteCount.intValue();
    }

}
