package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;


public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
