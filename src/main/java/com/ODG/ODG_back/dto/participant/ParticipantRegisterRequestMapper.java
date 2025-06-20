package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParticipantRegisterRequestMapper extends EntityMapper<ParticipantRegisterRequestDto, Participant> {
}
