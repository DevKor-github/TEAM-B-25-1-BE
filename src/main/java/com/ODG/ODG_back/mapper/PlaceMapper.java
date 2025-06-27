package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlaceMapper {

    PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

    @Mapping(source = "id", target = "placeId")
    PlaceResponseDto toDto(Place place);
}
