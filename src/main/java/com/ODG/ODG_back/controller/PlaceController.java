package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.place.response.GroupedPlacesResponse;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/places")
    public ResponseEntity<GroupedPlacesResponse> getPlacesByMidpoint(
            @PathVariable String inviteCode,
            @RequestParam(defaultValue = "1500") int radius,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "0") int refreshSeq
    ) {
        return ResponseEntity.ok(
                placeService.getPlacesByMidpointGrouped(inviteCode, radius, size, refreshSeq));
    }

}
