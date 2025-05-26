package com.ODG.ODG_back.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

