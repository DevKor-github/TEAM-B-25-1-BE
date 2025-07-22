package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.VoteService;
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
    public ResponseEntity<String> vote(@PathVariable String inviteCode,
                                       @RequestBody VoteRequestDto voteRequestDto,
                                       @CookieValue("access_token") String jwtToken) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        voteService.vote(inviteCode, userId, voteRequestDto);
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/result")
    public ResponseEntity<?> getVoteResults(@PathVariable String inviteCode) {
        List<VoteResultDto> voteResults = voteService.getVoteResults(inviteCode);
        return ResponseEntity.ok(voteResults);
    }

}