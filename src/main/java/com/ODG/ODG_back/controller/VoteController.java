package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import com.ODG.ODG_back.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings/{inviteCode}")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/vote")
    public ResponseEntity<String> vote(@PathVariable String inviteCode,
                                       @RequestBody VoteRequestDto voteRequest) {
        voteService.vote(inviteCode, voteRequest);
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/result")
    public ResponseEntity<?> getVoteResults(@PathVariable String inviteCode) {
        List<VoteResultDto> voteResults = voteService.getVoteResults(inviteCode);
        return ResponseEntity.ok(voteResults);
    }

}