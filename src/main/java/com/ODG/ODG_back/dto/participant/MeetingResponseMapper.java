package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.meeting.response.MeetingResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeetingResponseMapper extends EntityMapper<MeetingResponseDto, Meeting>{
        /*
        As MeetingResponseMapper extends EntityMapper, it inherits the methods to convert between MeetingResponseDto and Meeting entities.
        No additional methods are needed here as the EntityMapper interface already provides the necessary conversion methods.
         */
}
