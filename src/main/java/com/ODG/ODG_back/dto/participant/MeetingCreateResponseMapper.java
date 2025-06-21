package com.ODG.ODG_back.dto.participant;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MeetingCreateResponseMapper extends EntityMapper<MeetingCreateResponseDto, Meeting>{
    @Override
    @Mappings({
            @Mapping(source = "inviteCode", target = "linkCode")
    })
    MeetingCreateResponseDto toDto(Meeting meeting);
}
