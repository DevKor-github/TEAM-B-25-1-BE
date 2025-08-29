package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import com.ODG.ODG_back.domain.Vote;
import com.ODG.ODG_back.domain.enums.MeetingType;
import com.ODG.ODG_back.domain.enums.PlaceCategory;
import com.ODG.ODG_back.dto.place.SeedParams;
import com.ODG.ODG_back.dto.place.response.GroupedPlacesResponse;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.dto.place.response.PlaceSection;
import com.ODG.ODG_back.dto.place.response.PlaceSectionDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.external.kakao.KakaoLocalClient;
import com.ODG.ODG_back.external.kakao.KakaoLocalClient.KakaoPlaceDoc;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.ParticipantRepository;
import com.ODG.ODG_back.repository.PlaceRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import com.ODG.ODG_back.repository.VoteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final MeetingRepository meetingRepository;
    private final RecommendedMidpointRepository recommendedMidpointRepository;
    private final KakaoLocalClient kakaoClient;
    private final PlaceSlotAssigner placeSlotAssigner;
    private final PlaceRepository placeRepository;
    private final VoteRepository voteRepository;
    private final ParticipantRepository participantRepository;

    private final ObjectMapper objectMapper;

    private static final Map<PlaceSection, List<String>> SOCIAL_CODES = Map.of(
            PlaceSection.FOOD, List.of("FD6", "CE7"),
            PlaceSection.FUN, List.of("AT4", "CT1")
    );
    private static final String STUDY_KEYWORD = "스터디카페";

    private static final int MAX_PAGE = 3;

    @Transactional
    public GroupedPlacesResponse getPlacesByMidpointGrouped(
            String inviteCode,
            int radius,
            int size,
            Integer page,              // 1-based page; if null, defaults to 1
            boolean append,
            Long participantId         // nullable: when null, votedByMe will not be set
    ) {
        log.info("[PlaceService] getPlacesByMidpointGrouped inviteCode={}, radius={}, size={}, page={}, append={}",
                inviteCode, radius, size, page, append);

        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));

        log.info("[PlaceService] Found meeting id={}, type={}", meeting.getId(), meeting.getType());

        RecommendedMidpoint recommendedMidpoint = recommendedMidpointRepository.findTopByMeetingOrderByRecommendedAtDesc(
                        meeting)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RECOMMENDED_MIDPOINT_NOT_FOUND));
        Midpoint midpoint = recommendedMidpoint.getMidpoint();

        // 🔄 Auto-reset when midpoint changed: if existing candidates were seeded for a different midpoint,
        // wipe candidates (and votes tied to old slots) and start fresh, regardless of append flag.
        placeRepository.findFirstByMeetingOrderByIdAsc(meeting).ifPresent(first -> {
            try {
                SeedParams prev = objectMapper.readValue(first.getSeedParamsJson(), SeedParams.class);
                double prevLat = prev.getLat();
                double prevLng = prev.getLng();
                if (!nearlyEqual(prevLat, midpoint.getLatitude().doubleValue(), 1e-6) ||
                        !nearlyEqual(prevLng, midpoint.getLongitude().doubleValue(), 1e-6)) {
                    long vDel = voteRepository.deleteByMeeting(meeting);
                    long pDel = placeRepository.deleteByMeeting(meeting);
                    log.info("[PlaceService] Midpoint changed → reset candidates={}, votes={} (prevLat={},prevLng={}, nowLat={},nowLng={})",
                            pDel, vDel, prevLat, prevLng, midpoint.getLatitude(), midpoint.getLongitude());
                }
            } catch (Exception e) {
                log.warn("[PlaceService] Failed to parse previous seedParamsJson; skip midpoint change check: {}", e.toString());
            }
        });


        // ✅ Idempotent behavior: once candidates exist for the meeting, keep shared state even if append=false
        if (!append) {
            boolean hasSeeded = placeRepository.existsByMeeting(meeting);
            if (hasSeeded) {
                log.info("[PlaceService] Candidates already exist → treat append=false as append=true (keep shared state)");
                append = true;
            } else {
                long deleted = placeRepository.deleteByMeeting(meeting);
                log.info("[PlaceService] Initial seeding: cleared {} place candidates (votes preserved)", deleted);
            }
        }

        double lat = midpoint.getLatitude().doubleValue();
        double lng = midpoint.getLongitude().doubleValue();
        log.info("[PlaceService] Midpoint lat={}, lng={}", lat, lng);

        final int pageLocal = (page == null || page < 1) ? 1 : Math.min(page, MAX_PAGE);

        // 현재 참가자의 기존 투표 조회
        List<Integer> myVoteSlotNos = List.of();
        if (participantId != null) {
            Participant me = participantRepository.findById(participantId)
                    .orElse(null);
            if (me != null) {
                myVoteSlotNos = voteRepository.findAllByMeetingAndParticipant(meeting, me)
                        .stream()
                        .map(Vote::getSlotNo)
                        .toList();
            }
        }
        var myVoteSlotSet = new java.util.HashSet<>(myVoteSlotNos);

        List<PlaceSectionDto> sections = new ArrayList<>();
        if (meeting.getType() == MeetingType.SOCIAL) {
            log.info("[PlaceService] Processing SOCIAL meeting - page={}", pageLocal);
            for (PlaceSection sec : List.of(PlaceSection.FOOD, PlaceSection.FUN)) {
                String seedJson = toSeedJsonForCategory(SOCIAL_CODES.get(sec), lat, lng, radius, pageLocal, size);
                if (append && placeRepository.existsByMeetingAndSectionAndQueryTypeAndSeedParamsJson(meeting, sec, "category", seedJson)) {
                    log.info("[PlaceService] Skip duplicate page for section={} page={} (already seeded)", sec, pageLocal);
                    sections.add(new PlaceSectionDto(sec, sec.label, List.of()));
                    continue;
                }

                Map<String, KakaoPlaceDoc> seen = new HashMap<>();
                List<KakaoPlaceDoc> bucket = new ArrayList<>();
                for (String code : SOCIAL_CODES.get(sec)) {
                    var docs = fetchCategoryWithFallback(code, lat, lng, radius, pageLocal, size);
                    log.info("[PlaceService] Retrieved {} places (sec={}, code={}, page={})",
                            docs.size(), sec, code, pageLocal);

                    for (var d : docs) {
                        if (seen.putIfAbsent(d.getId(), d) != null) continue;
                        bucket.add(d);
                    }
                }
                int codesCount = SOCIAL_CODES.get(sec).size();
                int startSlotNo = append
                        ? pageStartSlotNo(sec, pageLocal, size, codesCount)
                        : resolveStartSlotNo(meeting, sec);
                log.info("[PlaceService] startSlotNo={} (append={}, page={}, size={}, codes={})", startSlotNo, append, pageLocal, size, (sec==PlaceSection.STUDY?1:codesCount));
                List<PlaceResponseDto> items = placeSlotAssigner.assignAndBuild(sec, lat, lng, bucket, startSlotNo);

                // votedByMe 세팅
                if (!myVoteSlotSet.isEmpty()) {
                    for (PlaceResponseDto p : items) {
                        p.setVotedByMe(myVoteSlotNos.contains(p.getSlotNo()));
                    }
                }

                for (PlaceResponseDto p : items) {
                    upsertCandidate(meeting, p.getSlotNo(), sec, "category", seedJson);
                }
                sections.add(new PlaceSectionDto(sec, sec.label, items));
            }
        } else if (meeting.getType() == MeetingType.PROJECT) {
            log.info("[PlaceService] Processing PROJECT meeting - page={}", pageLocal);
            String projectSeed = toSeedJsonForKeyword(lat, lng, Math.max(radius, 800), pageLocal, size);
            if (append && placeRepository.existsByMeetingAndSectionAndQueryTypeAndSeedParamsJson(meeting, PlaceSection.STUDY, "keyword", projectSeed)) {
                log.info("[PlaceService] Skip duplicate page for STUDY page={} (already seeded)", pageLocal);
                sections.add(new PlaceSectionDto(PlaceSection.STUDY, PlaceSection.STUDY.label, List.of()));
                boolean hasMore = pageLocal < MAX_PAGE && sections.stream().anyMatch(s -> s.getItems() != null && !s.getItems().isEmpty());
                log.info("[PlaceService] Finished grouping - sections={}, page={}, hasMore={}", sections.size(), pageLocal, hasMore);
                return new GroupedPlacesResponse(sections, myVoteSlotNos, pageLocal, hasMore);
            }

            var docs = kakaoClient.searchKeyword(STUDY_KEYWORD, lat, lng, Math.max(radius, 800), pageLocal, size);
            if (docs.isEmpty() && pageLocal != 1) {
                log.info("[PlaceService] Empty page={} -> fallback page=1 (keyword={})", pageLocal, STUDY_KEYWORD);
                docs = kakaoClient.searchKeyword(STUDY_KEYWORD, lat, lng, Math.max(radius, 800), 1, size);
            }
            log.info("[PlaceService] Retrieved {} places for PROJECT", docs.size());

            Map<String, KakaoPlaceDoc> seen = new HashMap<>();
            List<KakaoPlaceDoc> bucket = docs.stream()
                    .filter(d -> seen.putIfAbsent(d.getId(), d) == null)
                    .toList();


            int startSlotNo = append
                    ? pageStartSlotNo(PlaceSection.STUDY, pageLocal, size, 1)
                    : resolveStartSlotNo(meeting, PlaceSection.STUDY);
            log.info("[PlaceService] startSlotNo={} (append={}, page={}, size={}, codes={})", startSlotNo, append, pageLocal, size, 1);            List<PlaceResponseDto> items =
                    placeSlotAssigner.assignAndBuild(PlaceSection.STUDY, lat, lng, bucket, startSlotNo);

            if (!myVoteSlotSet.isEmpty()) {
                for (PlaceResponseDto p : items) {
                    p.setVotedByMe(myVoteSlotNos.contains(p.getSlotNo()));
                }
            }

            for (PlaceResponseDto p : items) {
                upsertCandidate(meeting, p.getSlotNo(), PlaceSection.STUDY, "keyword", projectSeed);
            }
            sections.add(new PlaceSectionDto(PlaceSection.STUDY, PlaceSection.STUDY.label, items));
        }
        boolean hasMore = pageLocal < MAX_PAGE && sections.stream().anyMatch(s -> s.getItems() != null && !s.getItems().isEmpty());
        log.info("[PlaceService] Finished grouping - sections={}, page={}, hasMore={}", sections.size(), pageLocal, hasMore);

        return new GroupedPlacesResponse(sections, myVoteSlotNos, pageLocal, hasMore);
    }

    private List<KakaoPlaceDoc> fetchCategoryWithFallback(String code, double lat, double lng, int radius, int page, int size) {
        var docs = kakaoClient.searchCategory(code, lat, lng, radius, page, size);
        if (docs.isEmpty() && page != 1) {
            log.info("[PlaceService] Empty page={} -> fallback page=1 (code={})", page, code);
            return kakaoClient.searchCategory(code, lat, lng, radius, 1, size);
        }
        return docs;
    }

    private int resolveStartSlotNo(Meeting meeting, PlaceSection sec) {
        int sectionBase = switch (sec) { case FOOD -> 1000; case FUN -> 2000; case STUDY -> 3000; default -> 9000; };
        return placeRepository.findMaxSlotNoByMeetingAndSection(meeting, sec)
                .map(max -> max + 1)
                .orElse(sectionBase);
    }

    private int pageStartSlotNo(PlaceSection sec, int pageLocal, int size, int codesCount) {
        int sectionBase = switch (sec) { case FOOD -> 1000; case FUN -> 2000; case STUDY -> 3000; default -> 9000; };
        int effectivePageSize = Math.max(1, size) * Math.max(1, codesCount);
        return sectionBase + (Math.max(1, pageLocal) - 1) * effectivePageSize;
    }

    private void upsertCandidate(Meeting meeting, int slotNo, PlaceSection section,
            String queryType, String seedParamsJson) {
        placeRepository.findByMeetingAndSlotNo(meeting, slotNo)
                .ifPresentOrElse(
                        p -> {},
                        () -> {
                            Place candidate = Place.builder()
                                    .meeting(meeting)
                                    .slotNo(slotNo)
                                    .section(section)
                                    .queryType(queryType)
                                    .seedParamsJson(seedParamsJson)
                                    .build();
                            placeRepository.save(candidate);
                        }
                );
    }

    private String toSeedJsonForCategory(List<String> codes, double lat, double lng,
            int radius, int page, int size) {
        SeedParams params = new SeedParams();
        params.setType("category");
        params.setCodes(codes);
        params.setLat(lat);
        params.setLng(lng);
        params.setRadius(radius);
        params.setPage(page);
        params.setSize(size);
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize SeedParams for category", e);
        }
    }

    private String toSeedJsonForKeyword(double lat, double lng,
            int radius, int page, int size) {
        SeedParams params = new SeedParams();
        params.setType("keyword");
        params.setKeyword(PlaceService.STUDY_KEYWORD);
        params.setLat(lat);
        params.setLng(lng);
        params.setRadius(radius);
        params.setPage(page);
        params.setSize(size);
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize SeedParams for keyword", e);
        }
    }

    private boolean nearlyEqual(double a, double b, double eps) {
        return Math.abs(a - b) <= eps;
    }
}
