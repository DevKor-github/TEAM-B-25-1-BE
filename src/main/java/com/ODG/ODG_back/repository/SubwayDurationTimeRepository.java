package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.SubwayDurationTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubwayDurationTimeRepository extends JpaRepository<SubwayDurationTime, Long> {

    Optional<SubwayDurationTime> findByStartAndEnd(Long start, Long end);
}
