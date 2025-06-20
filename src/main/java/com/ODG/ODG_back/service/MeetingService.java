package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;

import java.util.List;

public interface MeetingService {
    MeetingCreateResponseDto addMeeting(MeetingCreateRequestDto dto);
    MeetingCreateResponseDto modifyMeeting(MeetingCreateRequestDto dto);
    void deleteMeeting(String linkCode);
    MeetingCreateResponseDto getMeeting(String linkCode);
    List<ParticipantListResponseDto> getParticipants(String linkCode);
}
