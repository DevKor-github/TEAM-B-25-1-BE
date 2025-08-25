package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.PlaceService;
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
@Tag(name = "Place", description = "추천 장소 조회 API")
public class PlaceController {

    private final PlaceService placeService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "추천 장소 조회",
            description = "모임 중간 지점을 기반으로 추천 장소 목록을 조회합니다. "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 장소 조회 성공",
                    content = @Content(schema = @Schema(implementation = PlaceResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "인증 실패 (토큰 없음 또는 유효하지 않음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 모임 또는 중간지점", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping("/places")
    public ResponseEntity<List<PlaceResponseDto>> getPlacesByMidpoint(
            @Parameter(description = "모임 초대 코드")
            @PathVariable String inviteCode,
            @Parameter(description = "JWT 토큰 (쿠키에서 자동 추출, 아무 값 입력)")
            @CookieValue("access_token") String jwtToken
    ) {
        String userId = jwtTokenProvider.getUserId(jwtToken);
        return ResponseEntity.ok(placeService.getPlacesByMidpoint(inviteCode, userId));
    }

}
