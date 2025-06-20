package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import com.ODG.ODG_back.dto.participant.MeetingCreateRequestMapper;
import com.ODG.ODG_back.dto.participant.MeetingCreateResponseMapper;
import com.ODG.ODG_back.dto.participant.ParticipantListResponseMapper;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import com.ODG.ODG_back.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingCreateRequestMapper meetingCreateRequestMapper;
    private final MeetingCreateResponseMapper meetingCreateResponseMapper;
    private final ParticipantListResponseMapper participantListResponseMapper;

    @Override
    public MeetingCreateResponseDto addMeeting(MeetingCreateRequestDto dto) {
        try{
            Meeting meeting = meetingRepository.save(meetingCreateRequestMapper.toEntity(dto));
            return meetingCreateResponseMapper.toDto(meeting);
        } catch (Exception e) {
            // 예외 처리 로직 추가
            throw new RuntimeException("Failed to add meeting", e);
        }
    }

    @Override
    public MeetingCreateResponseDto modifyMeeting(MeetingCreateRequestDto dto) {
        try {
            Meeting updatedMeeting = meetingRepository.save(meetingCreateRequestMapper.toEntity(dto));
            return meetingCreateResponseMapper.toDto(updatedMeeting);
        }catch (Exception e) {
            // 예외 처리 로직 추가
            throw new RuntimeException("Failed to modify meeting", e);
        }
    }

    @Override
    public void deleteMeeting(String linkCode) {
        try {
            if (meetingRepository.findByInviteCode(linkCode).isEmpty()) {
                throw new IllegalArgumentException("Meeting with the given link code does not exist.");
            }
            meetingRepository.deleteByInviteCode(linkCode);
        } catch (Exception e) {
            // 예외 처리 로직 추가
            throw new RuntimeException("Failed to delete meeting", e);
        }
    }

    @Override
    public MeetingCreateResponseDto getMeeting(String linkCode) {
        try{
            Optional<Meeting> meeting = meetingRepository.findByInviteCode(linkCode);
            if(meeting.isEmpty()){
                throw new IllegalArgumentException("Meeting with the given link code does not exist.");
            }
            return meetingCreateResponseMapper.toDto(meeting.get());
        }catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve meeting", e);
        }
    }

    @Override
    public List<ParticipantListResponseDto> getParticipants(String linkCode) {
        List<Participant> participants;
        try {
            Optional<Meeting> meeting = meetingRepository.findByInviteCode(linkCode);
            if (meeting.isEmpty()) {
                throw new IllegalArgumentException("Meeting with the given link code does not exist.");
            }
            participants = meeting.get().getParticipants();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve participants", e);
        }
        return participants.stream().map(participantListResponseMapper::toDto).toList();
    }
}
