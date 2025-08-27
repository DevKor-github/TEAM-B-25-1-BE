package com.ODG.ODG_back.dto.place.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupedPlacesResponse {

    private List<PlaceSectionDto> sections;
    private final List<Integer> myVoteSlotNos; // nullable
    private final Integer page;         // nullable
    private final Boolean hasMore;      // nullable

}
