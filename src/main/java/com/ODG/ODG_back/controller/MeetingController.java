package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.meeting.request.MeetingCreateRequestDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingCreateResponseDto;
import com.ODG.ODG_back.dto.meeting.response.MeetingInfoResponseDto;
import com.ODG.ODG_back.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
@Tag(name = "Meeting", description = "모임 관리 API")
public class MeetingController {
    private final MeetingService meetingService;

    @Operation(
            summary = "새 모임 생성",
            description = "새로운 모임을 생성하고 고유한 링크 코드를 발급합니다. " +
                    "생성된 링크 코드로 참가자들이 모임에 참여할 수 있습니다." +
                    "인증이 필요하지 않는 공개 API입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "모임 생성 성공",
                    content = @Content(schema = @Schema(implementation = MeetingCreateResponseDto.class))
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<MeetingCreateResponseDto> createMeeting(
            @Parameter(
                    description = "모임 생성 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MeetingCreateRequestDto.class))
            )@RequestBody MeetingCreateRequestDto dto
    ) {
        return ResponseEntity.ok(meetingService.addMeeting(dto));
    }

    @Operation(
            summary = "모임 정보 조회",
            description = "링크 코드를 사용하여 모임의 기본 정보를 조회합니다. " +
                    "모임 제목, 목적 정보를 확인할 수 있습니다. " +
                    "인증이 필요하지 않는 공개 API입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "모임 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetingInfoResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping("/{linkCode}/info")
    public ResponseEntity<MeetingInfoResponseDto> getMeeting(
            @Parameter(description = "모임 링크 코드")
            @PathVariable String linkCode
    ) {
        return ResponseEntity.ok(meetingService.getMeeting(linkCode));
    }
}
