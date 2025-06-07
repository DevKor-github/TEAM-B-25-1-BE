package com.ODG.ODG_back.service;

import com.ODG.ODG_back.dto.participant.request.ParticipantDeletionRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantDto;

import java.util.List;

public interface ParticipantService {
    void addParticipant(String linkCode, ParticipantDto dto);
    void modifyParticipant(String linkCode, ParticipantDto dto);
    void deleteParticipant(String linkCode, ParticipantDeletionRequestDto dto);
}
