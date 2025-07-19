package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.BadRequestException;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.mapper.ParticipantRegisterRequestMapper;
import com.ODG.ODG_back.mapper.ParticipantRegisterResponseMapper;
import com.ODG.ODG_back.mapper.ParticipantUpdateMapper;
import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final ParticipantUpdateMapper participantUpdateMapper;
    private final MeetingRepository meetingRepository;
    private final ParticipantRegisterRequestMapper participantRegisterRequestMapper;
    private final ParticipantRegisterResponseMapper participantRegisterResponseMapper;

    // 새 참가자 참여
    public ParticipantRegisterResponseDto addParticipant(String linkCode, ParticipantRegisterRequestDto dto, String userId) {
        Participant participant = participantRegisterRequestMapper.toEntity(dto);
        participant.setUserId(userId);
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        participant.setMeeting(meeting);
        try {
            participantRepository.save(participant);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }
        return participantRegisterResponseMapper.toDto(participant);
    }

    // 참가자 정보 수정
    public void modifyParticipant(String linkCode, Long participantId, ParticipantUpdateRequestDto dto) {
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        Participant participant = participantRepository.findByIdAndMeeting(participantId, meeting)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));

        participantUpdateMapper.updateFromDto(dto, participant);
        participantRepository.save(participant);
    }

    // 참가자 삭제
    public void deleteParticipant(String linkCode, Long participantId) {
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        Participant participant = participantRepository.findByIdAndMeeting(participantId, meeting)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));

        participantRepository.delete(participant);
    }
}
