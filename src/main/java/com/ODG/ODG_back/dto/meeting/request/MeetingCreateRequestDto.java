package com.ODG.ODG_back.dto.meeting.request;

import com.ODG.ODG_back.domain.enums.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingCreateRequestDto {

    private String name;
    private MeetingType purpose;
}
