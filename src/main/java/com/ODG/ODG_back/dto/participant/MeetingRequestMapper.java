package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.request.MeetingRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeetingRequestMapper extends EntityMapper<MeetingRequestDto, Meeting>{
    /*
    As MeetingRequestMapper extends EntityMapper, it inherits the methods to convert between MeetingRequestDto and Meeting entities.
    No additional methods are needed here as the EntityMapper interface already provides the necessary conversion methods.
     */
}
