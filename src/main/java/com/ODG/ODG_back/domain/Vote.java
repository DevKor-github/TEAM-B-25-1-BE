package com.ODG.ODG_back.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer voteValue;

    private LocalDateTime votedAt;

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private Place place;

    @ManyToOne
    private Participant participant;
}

