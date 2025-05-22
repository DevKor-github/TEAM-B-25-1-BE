package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
