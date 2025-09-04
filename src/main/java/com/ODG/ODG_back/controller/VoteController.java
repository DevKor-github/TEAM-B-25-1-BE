package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.UnauthorizedException;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.VoteService;
import com.ODG.ODG_back.service.VoteService.VoteInstantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings/{inviteCode}")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/vote")
    public ResponseEntity<VoteInstantResponse> vote(
            @PathVariable String inviteCode,
            @RequestBody VoteRequestDto voteRequestDto,
            Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(voteService.vote(inviteCode, userId, voteRequestDto));
    }

    @GetMapping("/result")
    public ResponseEntity<List<PlaceResponseDto>> getVoteResults(@PathVariable String inviteCode) {
        List<PlaceResponseDto> voteResults = voteService.getVoteResults(inviteCode);
        return ResponseEntity.ok(voteResults);
    }

}