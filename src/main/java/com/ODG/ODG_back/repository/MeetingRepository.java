package com.ODG.ODG_back.repository;

import com.ODG.ODG_back.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    void deleteByInviteCode(String inviteCode);

    Optional<Meeting> findByInviteCode(String inviteCode);
}
