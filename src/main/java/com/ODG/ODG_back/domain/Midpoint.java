package com.ODG.ODG_back.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
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
