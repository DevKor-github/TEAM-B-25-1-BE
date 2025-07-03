package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.domain.enums.TransportType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    private TransportType transportType;

    private LocalDateTime joinedAt;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private String userId; // jwt에서 추출한 익명 사용자 ID (UUID)

    @ManyToOne
    private Meeting meeting;
}
