package com.ODG.ODG_back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
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

    public RecommendedMidpoint(Long id, double score, int rank, LocalDateTime recommendedAt, Meeting meeting, Midpoint midpoint) {
        this.id = id;
        this.score = score;
        this.rank = rank;
        this.recommendedAt = recommendedAt;
        this.meeting = meeting;
        this.midpoint = midpoint;
    }
}
