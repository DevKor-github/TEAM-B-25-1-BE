package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.domain.enums.TransportType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    private TransportType transport;

    private LocalDateTime joinedAt;

    @ManyToOne
    private Meeting meeting;
}
