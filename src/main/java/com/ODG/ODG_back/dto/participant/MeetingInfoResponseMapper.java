package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.response.MeetingInfoResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeetingInfoResponseMapper extends EntityMapper<MeetingInfoResponseDto, Meeting> {
}
