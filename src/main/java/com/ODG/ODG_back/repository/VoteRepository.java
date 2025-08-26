package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.Vote;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

//    @Query("SELECT new com.ODG.ODG_back.dto.vote.response.VoteResultDto(v.place.id, v.place.name, SUM(v.voteValue)) " +
//            "FROM Vote v WHERE v.meeting = :meeting GROUP BY v.place")
//    List<VoteResultDto> findVoteCountsByMeeting(@Param("meeting") Meeting meeting);

    @Query("""
                    SELECT new com.ODG.ODG_back.dto.vote.response.VoteResultDto(v.slotNo, COUNT(v)) 
                    FROM Vote v 
                    WHERE v.meeting = :meeting 
                    GROUP BY v.slotNo 
                    ORDER BY COUNT(v) DESC
            """)
    List<VoteResultDto> findVoteCountsByMeeting(@Param("meeting") Meeting meeting);

    Optional<Vote> findByMeetingAndSlotNoAndParticipant(Meeting meeting, Integer slotNo,
            Participant participant);

    int deleteByMeeting(Meeting meeting);
}
