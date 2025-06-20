package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParticipantUpdateMapper extends EntityMapper<ParticipantUpdateRequestDto, Participant>{
    /*As ParticipantMapper extends EntitiyMapper, it inherits the methods to convert between ParticipantDto andParticipant entities.
    No additional methods are needed here as the EntityMapper interface already provides the necessary conversion methods.
    */
}
