package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.VoteService;
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

import java.util.List;

@RestController
@RequestMapping("/meetings/{inviteCode}")
@RequiredArgsConstructor
@Tag(name = "Vote", description = "투표 관리 API")
public class VoteController {

    private final VoteService voteService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "투표 토글",
            description = "참가자가 특정 장소에 대해 투표를 생성하거나 이미 투표가 된 장소라면 삭제합니다. "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "투표 토글 성공"),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임 또는 참가자 또는 장소", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PostMapping("/vote")
    public ResponseEntity<Void> vote(
            @Parameter(description = "모임 초대 코드")
            @PathVariable String inviteCode,
            @Parameter(description = "투표 요청 데이터")
            @RequestBody VoteRequestDto voteRequestDto,
            @Parameter(description = "JWT 토큰 (쿠키에서 자동 추출, 아무 값 입력)")
            @CookieValue("access_token") String jwtToken) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        voteService.vote(inviteCode, userId, voteRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "투표 결과 조회",
            description = "특정 모임의 투표 결과를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "투표 결과 조회 성공",
                    content = @Content(schema = @Schema(implementation = VoteResultDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping("/result")
    public ResponseEntity<List<VoteResultDto>> getVoteResults(
            @Parameter(description = "모임 초대 코드")
            @PathVariable String inviteCode,
            @Parameter(description = "JWT 토큰 (쿠키에서 자동 추출, 아무 값 입력)")
            @CookieValue("access_token") String jwtToken
            ) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        List<VoteResultDto> voteResults = voteService.getVoteResults(inviteCode, userId);
        return ResponseEntity.ok(voteResults);
    }

}