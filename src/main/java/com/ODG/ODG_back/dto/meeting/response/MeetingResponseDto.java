package com.ODG.ODG_back.dto.meeting.response;

import com.ODG.ODG_back.domain.enums.MeetingType;

import java.time.LocalDateTime;

public class MeetingResponseDto {
    private Long meetingId;

    private String title;

    private MeetingType type;

    private String inviteCode;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

}
