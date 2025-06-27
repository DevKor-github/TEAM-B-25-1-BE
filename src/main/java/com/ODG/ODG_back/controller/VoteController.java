package com.ODG.ODG_back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetings/{inviteCode}/vote")
@RequiredArgsConstructor
public class VoteController {
    @GetMapping
    public String vote(@PathVariable String inviteCode) {
        return "Hello" + inviteCode;
    }
}
