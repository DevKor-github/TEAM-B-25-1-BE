package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingInfoResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.mapper.MeetingCreateRequestMapper;
import com.ODG.ODG_back.mapper.MeetingCreateResponseMapper;
import com.ODG.ODG_back.mapper.MeetingInfoResponseMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingCreateRequestMapper meetingCreateRequestMapper;
    private final MeetingCreateResponseMapper meetingCreateResponseMapper;
    private final MeetingInfoResponseMapper meetingInfoResponseMapper;

    public MeetingCreateResponseDto addMeeting(MeetingCreateRequestDto dto) {
        Meeting meeting = meetingRepository.save(meetingCreateRequestMapper.toEntity(dto));
        return meetingCreateResponseMapper.toDto(meeting);
    }

    public MeetingCreateResponseDto modifyMeeting(MeetingCreateRequestDto dto) {
        Meeting updatedMeeting = meetingRepository.save(meetingCreateRequestMapper.toEntity(dto));
        return meetingCreateResponseMapper.toDto(updatedMeeting);
    }

    public void deleteMeeting(String linkCode) {
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        meetingRepository.delete(meeting);
    }

    public MeetingInfoResponseDto getMeeting(String linkCode) {
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        return meetingInfoResponseMapper.toDto(meeting);
    }
}
