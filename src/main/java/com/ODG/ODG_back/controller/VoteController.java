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
    public ResponseEntity<Void> vote(@PathVariable String inviteCode,
                                       @RequestBody VoteRequestDto voteRequestDto,
                                       @CookieValue("access_token") String jwtToken) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        voteService.vote(inviteCode, userId, voteRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/result")
    public ResponseEntity<List<VoteResultDto>> getVoteResults(@PathVariable String inviteCode) {
        List<VoteResultDto> voteResults = voteService.getVoteResults(inviteCode);
        return ResponseEntity.ok(voteResults);
    }

}