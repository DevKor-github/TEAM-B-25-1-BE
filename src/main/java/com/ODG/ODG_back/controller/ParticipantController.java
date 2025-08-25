package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import com.ODG.ODG_back.security.jwt.JwtCookieUtil;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.ParticipantService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meetings/{linkCode}/participants")
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ParticipantRegisterResponseDto> addParticipant(
            @PathVariable String linkCode,
            @RequestBody ParticipantRegisterRequestDto requestDto,
            HttpServletResponse response
    ){
        String userId = UUID.randomUUID().toString();
        ParticipantRegisterResponseDto responseDto = participantService.addParticipant(linkCode, requestDto, userId);
        JwtCookieUtil.addTokenToCookie(response, responseDto.getAccessToken());

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
            @CookieValue("access_token") String jwtToken
    ) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        participantService.deleteParticipant(linkCode, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateParticipant(
            @PathVariable String linkCode,
            @RequestBody ParticipantUpdateRequestDto participantUpdateRequestDto,
            @CookieValue("access_token") String jwtToken // 쿠키에서 jwt 추출
    ){
        String userId = jwtTokenProvider.getUserId(jwtToken);
        participantService.modifyParticipant(linkCode, userId, participantUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<ParticipantListResponseDto>> getParticipants(@PathVariable String linkCode){
        List<ParticipantListResponseDto> participants = participantService.getParticipantsWithVoteStatus(linkCode);
        return ResponseEntity.ok(participants);
    }
}
