package com.ODG.ODG_back.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Midpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String line;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @OneToMany(mappedBy = "midpoint")
    private List<RecommendedMidpoint> recommendedMidpoints;

    @ManyToMany(mappedBy = "midpoints")
    private List<Place> places;
}
