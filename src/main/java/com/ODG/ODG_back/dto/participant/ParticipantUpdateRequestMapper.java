package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface ParticipantUpdateRequestMapper extends EntityMapper<ParticipantUpdateRequestDto, Participant>{
    @Override
    @Mappings({
            @Mapping(source = "participantId", target = "id"),
            @Mapping(source = "lat", target = "latitude"),
            @Mapping(source = "lng", target = "longitude")
    })
    Participant toEntity(ParticipantUpdateRequestDto dto);
}
