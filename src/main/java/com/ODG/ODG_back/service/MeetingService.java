package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.meeting.request.MeetingRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingResponseDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantDto;

import java.util.List;

public interface MeetingService {
    MeetingResponseDto addMeeting(MeetingRequestDto dto);
    MeetingResponseDto modifyMeeting(MeetingRequestDto dto);
    void deleteMeeting(String linkCode);
    MeetingResponseDto getMeeting(String linkCode);
    List<ParticipantDto> getParticipants(String linkCode);
}
