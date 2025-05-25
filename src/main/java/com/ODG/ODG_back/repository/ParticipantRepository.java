package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
