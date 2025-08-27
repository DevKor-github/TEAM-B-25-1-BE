package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.VoteService;
import com.ODG.ODG_back.service.VoteService.VoteInstantResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings/{inviteCode}")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/vote")
    public ResponseEntity<VoteInstantResponse> vote(@PathVariable String inviteCode,
            @RequestBody VoteRequestDto voteRequestDto, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String userId = jwtTokenProvider.getUserId(token);
        return ResponseEntity.ok(voteService.vote(inviteCode, userId, voteRequestDto));
    }

    @GetMapping("/result")
    public ResponseEntity<List<PlaceResponseDto>> getVoteResults(@PathVariable String inviteCode) {
        List<PlaceResponseDto> voteResults = voteService.getVoteResults(inviteCode);
        return ResponseEntity.ok(voteResults);
    }

}