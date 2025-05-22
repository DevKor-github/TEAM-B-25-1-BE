package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.service.MidpointService;
import com.ODG.ODG_back.strategy.MidpointStrategyType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings/{inviteCode}/midpoint")
@RequiredArgsConstructor
public class MidpointController {

    private final MidpointService midpointService;

    @GetMapping
    public ResponseEntity<?>getRecommendedMidpoints(@PathVariable String inviteCode,
                                                    @RequestParam MidpointStrategyType strategyType) {
        return ResponseEntity.ok(midpointService.getRecommendedMidpoints(inviteCode, strategyType));
    }

}
