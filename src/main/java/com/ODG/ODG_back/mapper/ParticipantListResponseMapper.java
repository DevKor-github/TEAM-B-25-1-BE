package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ParticipantListResponseMapper{
    @Mappings({
            @Mapping(source = "id", target = "participantId"),
            @Mapping(source = "latitude", target = "lat"),
            @Mapping(source = "longitude", target = "lng")
    })
    ParticipantListResponseDto toDto(Participant participant);
}
