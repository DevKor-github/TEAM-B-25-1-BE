package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.participant.request.ParticipantDeletionRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import com.ODG.ODG_back.service.MeetingServiceImpl;
import com.ODG.ODG_back.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/meetings/{linkCode}/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;
    private final MeetingServiceImpl meetingServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<ParticipantRegisterResponseDto> addParticipant(@PathVariable String linkCode, @RequestBody ParticipantRegisterRequestDto participantRegisterRequestDto){
        ParticipantRegisterResponseDto participantRegisterResponseDto = participantService.addParticipant(linkCode, participantRegisterRequestDto);
        return ResponseEntity.ok(participantRegisterResponseDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteParticipant(@PathVariable String linkCode, @RequestBody ParticipantDeletionRequestDto participantDeletionRequestDto){
        participantService.deleteParticipant(linkCode, participantDeletionRequestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateParticipant(@PathVariable String linkCode, @RequestBody ParticipantUpdateRequestDto participantUpdateRequestDto){
        participantService.modifyParticipant(linkCode, participantUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<ParticipantListResponseDto>> getParticipants(@PathVariable String linkCode){
        List<ParticipantListResponseDto> participants = meetingServiceImpl.getParticipants(linkCode);
        return ResponseEntity.ok(participants);
    }
}
