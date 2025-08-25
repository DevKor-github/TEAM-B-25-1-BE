package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MidpointMapper {

    @Mapping(source = "id", target = "midpointId")
    MidpointResponseDto toDto(Midpoint midpoint);

    @Mapping(target = "midpointId", source = "midpoint.id")
    @Mapping(target = "name",       source = "midpoint.name")
    @Mapping(target = "line",       source = "midpoint.line")
    @Mapping(target = "latitude",   expression = "java(midpoint.getLatitude())")
    @Mapping(target = "longitude",  expression = "java(midpoint.getLongitude())")
    @Mapping(target = "avgTime",         source = "avg")
    @Mapping(target = "totalDeviation",  source = "dev")
    @Mapping(target = "participantId",   source = "participantId")
    @Mapping(target = "selfTimeSeconds", source = "selfTimeSeconds")
    @Mapping(target = "participantCount", source = "participantCount")
    MidpointResponseDto toDtoSelfOnly(
        Midpoint midpoint,
        double   avg,
        double   dev,
        Long     participantId,
        int      selfTimeSeconds,
        int      participantCount
    );
}
