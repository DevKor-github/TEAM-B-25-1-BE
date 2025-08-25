package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.MidpointService;
import com.ODG.ODG_back.strategy.MidpointStrategyType;
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
@RequestMapping("/meetings/{inviteCode}/midpoint")
@RequiredArgsConstructor
@Tag(name = "Midpoint", description = "중간 지점 추천 API")
public class MidpointController {

    private final MidpointService midpointService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "중간 지점 조회",
            description = "모임 참가자들의 위치를 기반으로 중간 지점을 조회합니다. " +
                    "전략은 쿼리 파라미터로 전달됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "중간 지점 조회 성공",
                    content = @Content(schema = @Schema(implementation = MidpointResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping
    public ResponseEntity<MidpointResponseDto> getRecommendedMidpoints(
            @Parameter(description = "모임 링크 코드")
            @PathVariable String inviteCode,
            @Parameter(description = "JWT 토큰 (쿠키에서 자동 추출, 아무 값 입력)")
            @CookieValue("access_token") String jwtToken,
            @Parameter(description = "전략 타입")
            @RequestParam MidpointStrategyType strategyType) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        return ResponseEntity.ok(midpointService.getRecommendedMidpoints(inviteCode, strategyType, userId));
    }

}
