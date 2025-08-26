package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.domain.enums.MeetingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(indexes = {@Index(name = "idx_meeting_invite_code", columnList = "inviteCode")})
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private MeetingType type;

    @Column(unique = true)
    private String inviteCode;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<Participant> participants;

    @OneToMany(mappedBy = "meeting")
    private List<Vote> votes;

    @OneToMany(mappedBy = "meeting")
    private List<RecommendedMidpoint> recommendedMidpoints;


    @PrePersist
    private void generateInviteCode() {
        if (this.inviteCode == null) {
            this.inviteCode = java.util.Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(java.util.UUID.randomUUID().toString().getBytes());
        }
    }
}