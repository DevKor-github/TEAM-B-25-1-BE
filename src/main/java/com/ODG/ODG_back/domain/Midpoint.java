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
    private Long id;

    private String name;

    @Column(precision = 15, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 15, scale = 8)
    private BigDecimal longitude;

    private String line;

    private Double hubScore;

    public Double getHubScore() {
        return hubScore == null ? 0.0 : hubScore;
    }

    @ManyToMany(mappedBy = "midpoints")
    private List<Place> places;
}
