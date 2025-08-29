package com.ODG.ODG_back.dto.vote.response;

import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteResultResponse {
    private List<VoteResultDto> voteResults;
    private PlaceResponseDto finalPlace;
}
