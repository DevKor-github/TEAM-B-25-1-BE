package com.ODG.ODG_back.mapper;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface MeetingCreateRequestMapper{
    @Mappings({
            @Mapping(source = "name", target = "title"),
            @Mapping(source = "purpose", target = "type"),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "expiresAt", expression = "java(LocalDateTime.now().plusDays(7))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "inviteCode", ignore = true),
            @Mapping(target = "participants", ignore = true),
            @Mapping(target = "votes", ignore = true),
            @Mapping(target = "recommendedMidpoints", ignore = true)
    })
    Meeting toEntity(MeetingCreateRequestDto dto);
}
