package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.exception.custom.UnauthorizedException;
import com.ODG.ODG_back.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final ParticipantRepository participantRepository;

  public String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
    }
    Object principal = auth.getPrincipal();
    if (!(principal instanceof String userId) || userId.isBlank()) {
      throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
    }
    return userId;
  }

  public Long getCurrentParticipantId(Meeting meeting) {
    String userId = getCurrentUserId();
    Participant p = participantRepository
        .findByUserIdAndMeeting(userId, meeting)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));
    return p.getId();
  }

}
