package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParticipantRegisterResponseMapper extends EntityMapper<ParticipantRegisterResponseDto, Participant> {

}
