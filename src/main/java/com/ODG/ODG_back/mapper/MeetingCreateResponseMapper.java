package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MeetingCreateResponseMapper {

    @Mappings({
            @Mapping(source = "inviteCode", target = "linkCode")
    })
    MeetingCreateResponseDto toDto(Meeting meeting);
}
