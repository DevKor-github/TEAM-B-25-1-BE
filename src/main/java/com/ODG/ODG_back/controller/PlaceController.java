package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.place.response.GroupedPlacesResponse;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.UnauthorizedException;
import com.ODG.ODG_back.security.jwt.JwtTokenProvider;
import com.ODG.ODG_back.service.ParticipantService;
import com.ODG.ODG_back.service.PlaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meetings/{inviteCode}")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final ParticipantService participantService;

    @GetMapping("/places")
    public ResponseEntity<GroupedPlacesResponse> getPlacesByMidpoint(
            @PathVariable String inviteCode,
            @RequestParam(defaultValue = "1500") int radius,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "false") boolean append,
            Authentication auth
    ) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }
        String userId = (String) auth.getPrincipal();
        Long participantId = participantService.getParticipantId(userId);
        return ResponseEntity.ok(
                placeService.getPlacesByMidpointGrouped(inviteCode, radius, size, page, append, participantId));
    }

}
