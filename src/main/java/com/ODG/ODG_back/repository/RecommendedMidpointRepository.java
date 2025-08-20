package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendedMidpointRepository extends JpaRepository<RecommendedMidpoint, Long> {
    Optional<RecommendedMidpoint> findTopByMeetingOrderByRecommendedAtDesc(Meeting meeting);
}
