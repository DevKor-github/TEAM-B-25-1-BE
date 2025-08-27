package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.dto.place.response.PlaceSection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByMeetingAndSlotNo(Meeting meeting, Integer slotNo);

    List<Place> findAllByMeeting(Meeting meeting);

    long deleteByMeeting(Meeting meeting);

    @Query("select max(p.slotNo) from Place p where p.meeting = :meeting and p.section = :section")
    Optional<Integer> findMaxSlotNoByMeetingAndSection(@Param("meeting") Meeting meeting,
            @Param("section") PlaceSection section);

    boolean existsByMeetingAndSectionAndQueryTypeAndSeedParamsJson(
            Meeting meeting, PlaceSection section, String queryType, String seedParamsJson);

    Optional<Place> findFirstByMeetingOrderByIdAsc(Meeting meeting);

    boolean existsByMeeting(Meeting meeting);
}
