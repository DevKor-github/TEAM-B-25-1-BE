package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingInfoResponseDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.mapper.MeetingCreateRequestMapper;
import com.ODG.ODG_back.mapper.MeetingCreateResponseMapper;
import com.ODG.ODG_back.mapper.MeetingInfoResponseMapper;
import com.ODG.ODG_back.mapper.ParticipantListResponseMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingCreateRequestMapper meetingCreateRequestMapper;
    private final MeetingCreateResponseMapper meetingCreateResponseMapper;
    private final ParticipantListResponseMapper participantListResponseMapper;
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
                .orElseThrow(() -> new NotFoundException("해당 코드의 Meeting이 존재하지 않습니다."));
        meetingRepository.delete(meeting);
    }

    public MeetingInfoResponseDto getMeeting(String linkCode) {
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException("해당 코드의 Meeting이 존재하지 않습니다."));
        return meetingInfoResponseMapper.toDto(meeting);
    }

    public List<ParticipantListResponseDto> getParticipants(String linkCode) {
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException("해당 코드의 Meeting이 존재하지 않습니다."));
        return meeting.getParticipants().stream()
                .map(participantListResponseMapper::toDto)
                .toList();
    }
}
