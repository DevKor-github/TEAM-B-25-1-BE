package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.meeting.request.MeetingRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingResponseDto;
import com.ODG.ODG_back.dto.participant.MeetingRequestMapper;
import com.ODG.ODG_back.dto.participant.MeetingResponseMapper;
import com.ODG.ODG_back.dto.participant.ParticipantMapper;
import com.ODG.ODG_back.dto.participant.request.ParticipantDto;
import com.ODG.ODG_back.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingRequestMapper meetingRequestMapper;
    private final MeetingResponseMapper meetingResponseMapper;
    private final ParticipantMapper participantMapper;

    @Override
    public MeetingResponseDto addMeeting(MeetingRequestDto dto) {
        try{
            Meeting meeting = meetingRepository.save(meetingRequestMapper.toEntity(dto));
            return meetingResponseMapper.toDto(meeting);
        } catch (Exception e) {
            // 예외 처리 로직 추가
            throw new RuntimeException("Failed to add meeting", e);
        }
    }

    @Override
    public MeetingResponseDto modifyMeeting(MeetingRequestDto dto) {
        try {
            Meeting updatedMeeting = meetingRepository.save(meetingRequestMapper.toEntity(dto));
            return meetingResponseMapper.toDto(updatedMeeting);
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
    public MeetingResponseDto getMeeting(String linkCode) {
        try{
            Optional<Meeting> meeting = meetingRepository.findByInviteCode(linkCode);
            if(meeting.isEmpty()){
                throw new IllegalArgumentException("Meeting with the given link code does not exist.");
            }
            return meetingResponseMapper.toDto(meeting.get());
        }catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve meeting", e);
        }
    }

    @Override
    public List<ParticipantDto> getParticipants(String linkCode) {
        try{
            Optional<Meeting> meeting = meetingRepository.findByInviteCode(linkCode);
            if(meeting.isEmpty()){
                throw new IllegalArgumentException("Meeting with the given link code does not exist.");
            }

            List<Participant> participants = meeting.get().getParticipants();

            return participants.stream().map(participantMapper::toDto).toList();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve participants", e);
        }
    }
}
