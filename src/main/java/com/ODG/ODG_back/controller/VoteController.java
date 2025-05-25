package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.vote.request.VoteRequestDTO;
import com.ODG.ODG_back.dto.vote.response.VoteResultDTO;
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
                                  @RequestBody VoteRequestDTO voteRequest) {
        try {
            voteService.vote(inviteCode, voteRequest);
            return ResponseEntity.ok().body("OK");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }


    }

    @GetMapping("/result")
    public ResponseEntity<?> getVoteResults(@PathVariable String inviteCode) {
        try {
            List<VoteResultDTO> voteResults = voteService.getVoteResults(inviteCode);
            return ResponseEntity.ok(voteResults);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
