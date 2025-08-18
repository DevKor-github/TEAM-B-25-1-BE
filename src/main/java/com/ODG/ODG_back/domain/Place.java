package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.domain.enums.PlaceCategory;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String placeId;

    private String name;

    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @ManyToMany
    private List<Midpoint> midpoints;
}
