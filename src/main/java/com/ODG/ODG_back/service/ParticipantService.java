package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.BadRequestException;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.mapper.ParticipantRegisterRequestMapper;
import com.ODG.ODG_back.mapper.ParticipantRegisterResponseMapper;
import com.ODG.ODG_back.mapper.ParticipantUpdateMapper;
import com.ODG.ODG_back.dto.participant.request.ParticipantDeletionRequestDto;
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

    public ParticipantRegisterResponseDto addParticipant(String linkCode, ParticipantRegisterRequestDto dto) {
        Participant participant = participantRegisterRequestMapper.toEntity(dto);
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

    public void modifyParticipant(String linkCode, ParticipantUpdateRequestDto dto) {
        Participant existingParticipant = participantRepository.findById(dto.getParticipantId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));

        if (!existingParticipant.getMeeting().equals(meeting)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        participantUpdateMapper.updateFromDto(dto, existingParticipant);
        participantRepository.save(existingParticipant);
    }

    public void deleteParticipant(String linkCode, ParticipantDeletionRequestDto dto) {
        Participant existingParticipant = participantRepository.findById(dto.getParticipantId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));
        Meeting meeting = meetingRepository.findByInviteCode(linkCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));

        if (!existingParticipant.getMeeting().equals(meeting)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        participantRepository.deleteById(dto.getParticipantId());
    }
}
