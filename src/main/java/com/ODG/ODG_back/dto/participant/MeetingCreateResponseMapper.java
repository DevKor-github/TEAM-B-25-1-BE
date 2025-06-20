package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeetingCreateResponseMapper extends EntityMapper<MeetingCreateResponseDto, Meeting>{
        /*
        As MeetingCreateResponseMapper extends EntityMapper, it inherits the methods to convert between MeetingCreateResponseDto and Meeting entities.
        No additional methods are needed here as the EntityMapper interface already provides the necessary conversion methods.
         */
}
