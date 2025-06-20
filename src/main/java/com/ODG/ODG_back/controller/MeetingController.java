package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import com.ODG.ODG_back.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;

    @PostMapping("/")
    public ResponseEntity<MeetingCreateResponseDto> createMeeting(@RequestBody MeetingCreateRequestDto dto) {
        return ResponseEntity.ok(meetingService.addMeeting(dto));
    }
}
