package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.request.ParticipantModificationRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface ParticipantRepository {
    List<Participant> getParticipants(String linkCode);
    void addParticipant(String linkCode, ParticipantResponseDto dto);
    void deleteParticipant(String linkCode, BigInteger participantId);
    void modifyParticipant(String linkCode, ParticipantModificationRequestDto dto);
}
