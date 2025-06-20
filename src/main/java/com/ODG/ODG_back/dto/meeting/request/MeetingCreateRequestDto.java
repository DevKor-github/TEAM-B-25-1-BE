package com.ODG.ODG_back.dto.meeting.request;

import com.ODG.ODG_back.domain.enums.MeetingType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MeetingCreateRequestDto {
    private String title;
    private MeetingType type;
}
