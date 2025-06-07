package com.ODG.ODG_back.dto.meeting.request;

import com.ODG.ODG_back.domain.enums.MeetingType;

import java.time.LocalDateTime;

public class MeetingRequestDto {
    private Long meetingId;

    private String title;

    private MeetingType type;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

}
