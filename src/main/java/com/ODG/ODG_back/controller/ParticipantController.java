package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.UnauthorizedException;
import com.ODG.ODG_back.security.jwt.JwtCookieUtil;
import com.ODG.ODG_back.security.jwt.JwtTokenFilter;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.ParticipantService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meetings/{linkCode}/participants")
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/register")
    public ResponseEntity<ParticipantRegisterResponseDto> addParticipant(
            @PathVariable String linkCode,
            @RequestBody ParticipantRegisterRequestDto requestDto
    ) {
        String userId = UUID.randomUUID().toString();
        ParticipantRegisterResponseDto responseDto = participantService.addParticipant(linkCode,
                requestDto, userId);

        log.info("resp pid={}, nick={}, tokenNull={}, ttl={}",
                responseDto.getParticipantId(),
                responseDto.getParticipantName(),
                responseDto.getAccessToken() == null,
                responseDto.getExpiresIn()
        );

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteParticipant(
            @PathVariable String linkCode,
            Authentication auth
    ) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }
        String userId = (String) auth.getPrincipal();
        participantService.deleteParticipant(linkCode, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateParticipant(
            @PathVariable String linkCode,
            @RequestBody ParticipantUpdateRequestDto participantUpdateRequestDto,
            Authentication auth
    ) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }
        String userId = (String) auth.getPrincipal();
        participantService.modifyParticipant(linkCode, userId, participantUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<ParticipantListResponseDto>> getParticipants(
            @PathVariable String linkCode) {
        List<ParticipantListResponseDto> participants = participantService.getParticipantsWithVoteStatus(
                linkCode);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/me")
    public ResponseEntity<ParticipantListResponseDto> getMyInfo(
            @PathVariable String linkCode,
            Authentication auth
    ) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }
        String userId = (String) auth.getPrincipal();
        ParticipantListResponseDto participant = participantService.getMyInfo(linkCode, userId);
        return ResponseEntity.ok(participant);
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<ParticipantListResponseDto> getParticipant(
            @PathVariable String linkCode,
            @PathVariable Long participantId
    ) {
        ParticipantListResponseDto participant = participantService.getParticipant(linkCode, participantId);
        return ResponseEntity.ok(participant);
    }
}
