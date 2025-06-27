package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipantRegisterResponseMapper{
    @Mapping(source = "id", target = "participantId")
    ParticipantRegisterResponseDto toDto(Participant entity);
}
