package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface ParticipantUpdateMapper extends EntityMapper<ParticipantUpdateRequestDto, Participant>{
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ParticipantUpdateRequestDto dto, @MappingTarget Participant participant);
}
