package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MidpointMapper {

    @Mapping(source = "id", target = "midpointId")
    MidpointResponseDto toDto(Midpoint midpoint);
}
