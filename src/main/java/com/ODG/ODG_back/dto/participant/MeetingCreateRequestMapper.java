package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeetingCreateRequestMapper extends EntityMapper<MeetingCreateRequestDto, Meeting>{
    /*
    As MeetingCreateRequestMapper extends EntityMapper, it inherits the methods to convert between MeetingCreateRequestDto and Meeting entities.
    No additional methods are needed here as the EntityMapper interface already provides the necessary conversion methods.
     */
}
