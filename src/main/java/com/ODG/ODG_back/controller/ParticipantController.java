package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.participant.request.ParticipantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/meetings/{linkCode}/participants")
@RequiredArgsConstructor
public class ParticipantController {

    @PostMapping("/register")
    public ResponseEntity<?> addParticipant(@PathVariable String linkCode, @RequestBody ParticipantDto participantDto){
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete")
    public void deleteParticipant(@PathVariable String linkCode, @RequestParam Long participantId){

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/update")
    public void updateParticipant(@PathVariable String linkCode, @RequestBody ParticipantDto participantDto){
        
    }

    @ResponseStatus(org.springframework.http.HttpStatus.OK)
    @GetMapping("/")
    public ResponseEntity<ParticipantDto> getParticipants(@PathVariable String linkCode){
        return ResponseEntity.ok(null);
    }
}
