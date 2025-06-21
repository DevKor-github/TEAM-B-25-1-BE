package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ParticipantRegisterResponseMapper extends EntityMapper<ParticipantRegisterResponseDto, Participant> {
    @Override
    @Mapping(source = "id", target = "participantId")
    ParticipantRegisterResponseDto toDto(Participant entity);
}
