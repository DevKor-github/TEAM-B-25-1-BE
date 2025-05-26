package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.participant.request.ParticipantAdministrationRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantDeletionRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantModificationRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantResponseDto;

import java.util.List;

public interface ParticipantService {
    ParticipantResponseDto addParticipant(String linkCode, ParticipantAdministrationRequestDto dto);
    void modifyParticipant(String linkCode, ParticipantModificationRequestDto dto);
    void deleteParticipant(String linkCode, ParticipantDeletionRequestDto dto);
    List<ParticipantResponseDto> getParticipants(String linkCode);
}
