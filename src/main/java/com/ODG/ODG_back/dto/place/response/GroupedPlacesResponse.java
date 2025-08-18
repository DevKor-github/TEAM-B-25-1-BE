package com.ODG.ODG_back.dto.place.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupedPlacesResponse {
  private List<PlaceSectionDto> sections;

}
