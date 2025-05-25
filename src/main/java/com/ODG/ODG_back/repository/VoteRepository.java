package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByMeetingAndPlaceAndParticipant(Meeting meeting, Place place, Participant participant);

    @Query("SELECT v.place, SUM(v.voteValue) FROM Vote v WHERE v.meeting = :meeting GROUP BY v.place")
    List<Object[]> findVoteCountsByMeeting(@Param("meeting") Meeting meeting);
}
