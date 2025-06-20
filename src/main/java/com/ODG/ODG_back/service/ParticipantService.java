package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.participant.request.ParticipantDeletionRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;

public interface ParticipantService {
    ParticipantRegisterResponseDto addParticipant(String linkCode, ParticipantRegisterRequestDto dto);
    void modifyParticipant(String linkCode, ParticipantUpdateRequestDto dto);
    void deleteParticipant(String linkCode, ParticipantDeletionRequestDto dto);
}
