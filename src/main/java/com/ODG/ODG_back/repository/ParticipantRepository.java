package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByUserIdAndMeeting(String userId, Meeting meeting);

    Optional<Participant> findByUserId(String userId);

    @Query("SELECT new com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto(p.id, p.name, p.transportType, p.latitude, p.longitude, "
            +
            "CASE WHEN v.participant.id IS NOT NULL THEN true ELSE false END) " +
            "FROM Participant p LEFT JOIN Vote v ON p.id = v.participant.id " +
            "WHERE p.meeting.inviteCode = :linkCode")
    List<ParticipantListResponseDto> findParticipantsWithVoteStatus(
            @Param("linkCode") String linkCode);
}
