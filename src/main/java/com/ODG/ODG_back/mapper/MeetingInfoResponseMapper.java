package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.response.MeetingInfoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MeetingInfoResponseMapper{
    @Mappings({
            @Mapping(source = "title", target = "name"),
            @Mapping(source = "type", target = "purpose")
    })
    MeetingInfoResponseDto toDto(Meeting meeting);
}
