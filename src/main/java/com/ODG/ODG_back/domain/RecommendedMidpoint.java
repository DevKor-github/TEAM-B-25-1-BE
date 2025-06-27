package com.ODG.ODG_back.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RecommendedMidpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;
    @Column(name = "midpoint_rank")
    private int rank;

    private LocalDateTime recommendedAt;

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private Midpoint midpoint;
}
