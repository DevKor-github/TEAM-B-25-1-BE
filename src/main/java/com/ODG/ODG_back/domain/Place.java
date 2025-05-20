package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.domain.enums.PlaceCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @ManyToMany
    private List<Midpoint> midpoints;
}
