package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.domain.enums.MeetingType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private MeetingType type;

    private String inviteCode;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<Participant> participants;

    @OneToMany(mappedBy = "meeting")
    private List<Vote> votes;

    @OneToMany(mappedBy = "meeting")
    private List<RecommendedMidpoint> recommendedMidpoints;
}