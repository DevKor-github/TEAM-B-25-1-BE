package com.ODG.ODG_back.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Integer voteValue;

    private LocalDateTime votedAt;

    @Column(nullable = false)
    private Integer slotNo;

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private Participant participant;

    public Vote(Meeting meeting, Integer slotNo, Participant participant) {
        this.meeting = meeting;
        this.slotNo = slotNo;
//        this.place = place;
        this.participant = participant;
        this.voteValue = 1; // 새 투표 시 voteValue를 1로 고정
        this.votedAt = LocalDateTime.now(); // 투표 생성 시간 설정
    }
}

