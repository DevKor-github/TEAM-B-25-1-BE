package com.ODG.ODG_back.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class RecommendedMidpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;

    private int rank;

    private LocalDateTime recommendedAt;

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private Midpoint midpoint;
}
