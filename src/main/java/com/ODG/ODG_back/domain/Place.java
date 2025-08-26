package com.ODG.ODG_back.domain;

import com.ODG.ODG_back.dto.place.response.PlaceSection;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "place",
        uniqueConstraints = @UniqueConstraint(name = "uk_meeting_slot", columnNames = {"meeting_id", "slot_no"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Column(name = "slot_no", nullable = false)
    private Integer slotNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "section", nullable = false)
    private PlaceSection section;

    @Column(name = "query_type", nullable = false)
    private String queryType;

    @Column(name = "seed_params_json", nullable = false, columnDefinition = "json")
    private String seedParamsJson;

    @Builder
    public Place(Meeting meeting, Integer slotNo, PlaceSection section, String queryType, String seedParamsJson) {
        this.meeting = meeting;
        this.slotNo = slotNo;
        this.section = section;
        this.queryType = queryType;
        this.seedParamsJson = seedParamsJson;
    }

}
