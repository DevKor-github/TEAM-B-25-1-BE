package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.meeting.request.MeetingRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingResponseDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;

import java.util.List;

public interface MeetingService {
    MeetingResponseDto addMeeting(MeetingRequestDto dto);
    MeetingResponseDto modifyMeeting(MeetingRequestDto dto);
    void deleteMeeting(String linkCode);
    MeetingResponseDto getMeeting(String linkCode);
    List<ParticipantListResponseDto> getParticipants(String linkCode);
}
