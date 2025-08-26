package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByMeetingAndSlotNo(Meeting meeting, Integer slotNo);

    List<Place> findAllByMeeting(Meeting meeting);
}
