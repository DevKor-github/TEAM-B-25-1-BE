package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.participant.request.ParticipantRegisterRequestDto;
import com.ODG.ODG_back.dto.participant.request.ParticipantUpdateRequestDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantListResponseDto;
import com.ODG.ODG_back.dto.participant.response.ParticipantRegisterResponseDto;
import com.ODG.ODG_back.security.jwt.JwtCookieUtil;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meetings/{linkCode}/participants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Participant", description = "모임 참가자 관리 API")
public class ParticipantController {

    private final ParticipantService participantService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "모임 참가자 등록",
            description = "모임에 새로운 참가자를 등록하고 JWT 토큰을 쿠키로 설정합니다. " +
                    "등록 성공 시 access_token 쿠키가 자동으로 브라우저에 저장됩니다. " +
                    "뿐만 아니라 json으로 access_token을 반환합니다. " +
                    "인증이 필요한 타 api에선 쿠키에서 access_token을 받으니 유의하십시오. " +
                    "인증이 필요하지 않는 공개 API입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "참가자 등록 성공",
                    content = @Content(schema = @Schema(implementation = ParticipantRegisterResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<ParticipantRegisterResponseDto> addParticipant(
            @Parameter(description = "모임 링크 코드")
            @PathVariable String linkCode,
            @Parameter(description = "참가자 등록 정보")
            @RequestBody ParticipantRegisterRequestDto requestDto,
            HttpServletResponse response
    ){
        String userId = UUID.randomUUID().toString();
        ParticipantRegisterResponseDto responseDto = participantService.addParticipant(linkCode, requestDto, userId);
        JwtCookieUtil.addTokenToCookie(response, responseDto.getAccessToken());

        log.info("resp pid={}, nick={}, tokenNull={}, ttl={}",
            responseDto.getParticipantId(),
            responseDto.getParticipantName(),
            responseDto.getAccessToken() == null,
            responseDto.getExpiresIn()
        );

        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "모임 참가자 삭제",
            description = "현재 로그인한 참가자를 모임에서 제거합니다. " +
                    "쿠키의 JWT 토큰을 사용하여 인증을 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가자 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임 또는 참가자", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteParticipant(
            @Parameter(description = "모임 링크 코드")
            @PathVariable String linkCode,
            @Parameter(description = "JWT 토큰 (쿠키에서 자동 추출, 아무 값 입력)")
            @CookieValue("access_token") String jwtToken
    ) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        participantService.deleteParticipant(linkCode, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "참가자 정보 수정",
            description = "현재 로그인한 참가자의 정보를 수정합니다. " +
                    "쿠키의 JWT 토큰을 사용하여 본인 인증을 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가자 정보 수정 성공"),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임 또는 참가자", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PutMapping("/update")
    public ResponseEntity<Void> updateParticipant(
            @Parameter(description = "모임 링크 코드")
            @PathVariable String linkCode,
            @Parameter(description = "수정할 참가자 정보")
            @RequestBody ParticipantUpdateRequestDto participantUpdateRequestDto,
            @Parameter(description = "JWT 토큰 (쿠키에서 자동 추출, 아무 값 입력)")
            @CookieValue("access_token") String jwtToken // 쿠키에서 jwt 추출
    ){
        String userId = jwtTokenProvider.getUserId(jwtToken);
        participantService.modifyParticipant(linkCode, userId, participantUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "모임 참가자 목록 조회",
            description = "특정 모임의 모든 참가자 목록과 투표 상태를 조회합니다. "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "참가자 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ParticipantListResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<ParticipantListResponseDto>> getParticipants(
            @Parameter(description = "모임 링크 코드")
            @PathVariable String linkCode,
            @Parameter(description = "JWT 토큰 (쿠키에서 자동 추출, 아무 값 입력)")
            @CookieValue("access_token") String jwtToken
    ){
        String userId = jwtTokenProvider.getUserId(jwtToken);
        List<ParticipantListResponseDto> participants = participantService.getParticipantsWithVoteStatus(linkCode, userId);
        return ResponseEntity.ok(participants);
    }
}
