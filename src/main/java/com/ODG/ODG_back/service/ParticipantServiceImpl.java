package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.ParticipantRegisterRequestMapper;
import com.ODG.ODG_back.dto.participant.ParticipantRegisterResponseMapper;
import com.ODG.ODG_back.dto.participant.ParticipantUpdateMapper;
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
public class ParticipantServiceImpl implements ParticipantService{
    private final ParticipantRepository participantRepository;
    private final ParticipantUpdateMapper participantUpdateMapper;
    private final MeetingRepository meetingRepository;
    private final ParticipantRegisterRequestMapper participantRegisterRequestMapper;
    private final ParticipantRegisterResponseMapper participantRegisterResponseMapper;

    @Override
    public ParticipantRegisterResponseDto addParticipant(String linkCode, ParticipantRegisterRequestDto dto) {
        Participant participant;
        try {
            participant = participantRegisterRequestMapper.toEntity(dto);
            Meeting meeting = meetingRepository.findByInviteCode(linkCode).orElseThrow(
                    () -> new IllegalArgumentException("Meeting with the given link code does not exist.")
            );
            participant.setMeeting(meeting);
            participantRepository.save(participant);
        } catch (DataIntegrityViolationException e) {
            // Handle the case where the participant already exists
            throw new IllegalArgumentException("Participant already exists with the given details.");
        } catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("An error occurred while adding the participant: " + e.getMessage());
        }
        return participantRegisterResponseMapper.toDto(participant);
    }

    @Override
    public void modifyParticipant(String linkCode, ParticipantUpdateRequestDto dto) {
        try{
            Participant participant = participantUpdateMapper.toEntity(dto);

            if(!participantRepository.existsById(dto.getId())){
                throw new IllegalArgumentException("Participant with the given ID does not exist.");
            }

            participantRepository.save(participant);

        }catch (Exception e){
            // Handle exceptions such as participant not found or other errors
            throw new RuntimeException("An error occurred while modifying the participant: " + e.getMessage());
        }
    }

    @Override
    public void deleteParticipant(String linkCode, ParticipantDeletionRequestDto dto) {
        try{
            if(!participantRepository.existsById(dto.getParticipantId())){
                throw new IllegalArgumentException("Participant with the given ID does not exist.");
            }

            participantRepository.deleteById(dto.getParticipantId());
        } catch (DataIntegrityViolationException e) {
            // Handle the case where the participant cannot be deleted due to foreign key constraints
            throw new IllegalArgumentException("Cannot delete participant due to existing references.");
        } catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("An error occurred while deleting the participant: " + e.getMessage());
        }
    }
}
