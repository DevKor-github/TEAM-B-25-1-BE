package com.ODG.ODG_back.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = { @Index(name = "idx_start_end", columnList = "start,end") })
public class SubwayDurationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    Long start;

    @Column(nullable = false)
    Long end;

    int shortestDurationTime; // 최소 소요 시간
    int transferCountForShortestDuration; // 최소 소요 시간일 때 환승 횟수

    @Column(nullable = false, length = 2000)
    String shortestDurationTimePath; // 최소 소요 시간일 때 경로

    int durationTimeForMinimumTransfer; // 최소 환승 횟수일 때 소요 시간
    int minimumTransferCount; // 최소 환승 횟수

    @Column(nullable = false, length = 2000)
    String minimumTransferPath; // 최소 환승 횟수일 때 경로
}
