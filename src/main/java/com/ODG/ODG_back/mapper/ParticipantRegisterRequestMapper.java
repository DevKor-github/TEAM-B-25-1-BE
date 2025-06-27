package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface ParticipantRegisterRequestMapper{
    @Mappings({
            @Mapping(source = "lat", target = "latitude"),
            @Mapping(source = "lng", target = "longitude"),
            @Mapping(target = "joinedAt", expression = "java(LocalDateTime.now())"),
    })
    Participant toEntity(ParticipantRegisterRequestDto dto);
}
