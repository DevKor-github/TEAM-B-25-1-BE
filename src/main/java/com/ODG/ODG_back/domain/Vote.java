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

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private Place place;

    @ManyToOne
    private Participant participant;

    public Vote(Meeting meeting, Place place, Participant participant) {
        this.meeting = meeting;
        this.place = place;
        this.participant = participant;
        this.voteValue = 1; // 새 투표 시 voteValue를 1로 고정
        this.votedAt = LocalDateTime.now(); // 투표 생성 시간 설정
    }
    public void toggleVoteValue() {
        this.voteValue = (this.voteValue == 1) ? 0 : 1;
    }
}

