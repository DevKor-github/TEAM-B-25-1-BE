package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.domain.enums.TransportType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)

    private TransportType transport_type;

    private LocalDateTime joinedAt;

    private BigDecimal latitude;
    private BigDecimal longitude;
  
    @ManyToOne
    private Meeting meeting;
}
