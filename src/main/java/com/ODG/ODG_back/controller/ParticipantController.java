package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import com.ODG.ODG_back.security.jwt.JwtCookieUtil;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.MeetingService;
import com.ODG.ODG_back.service.ParticipantService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meetings/{linkCode}/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;
    private final MeetingService meetingService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ParticipantRegisterResponseDto> addParticipant(
            @PathVariable String linkCode,
            @RequestBody ParticipantRegisterRequestDto requestDto,
            HttpServletResponse response
    ){
        String userId = UUID.randomUUID().toString();
        ParticipantRegisterResponseDto responseDto = participantService.addParticipant(linkCode, requestDto, userId);
        String token = jwtTokenProvider.createToken(userId);
        JwtCookieUtil.addTokenToCookie(response, token);
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
        List<ParticipantListResponseDto> participants = meetingService.getParticipants(linkCode);
        return ResponseEntity.ok(participants);
    }
}
