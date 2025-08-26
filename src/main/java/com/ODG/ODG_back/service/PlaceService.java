package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
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
import java.util.Random;
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

    private final ObjectMapper objectMapper;

    private static final Map<PlaceSection, List<String>> SOCIAL_CODES = Map.of(
            PlaceSection.FOOD, List.of("FD6", "CE7"),
            PlaceSection.FUN, List.of("AT4", "CT1")
    );
    private static final String STUDY_KEYWORD = "스터디카페";

    private static final int MAX_PAGE = 3;

    private static int pageFromSeq(int refreshSeq, int maxPage) {
        return 1 + (refreshSeq % MAX_PAGE);
    }

    @Transactional
    public GroupedPlacesResponse getPlacesByMidpointGrouped(String inviteCode, int radius,
            int size, int refreshSeq) {
        log.info(
                "[PlaceService] getPlacesByMidpointGrouped inviteCode={}, radius={}, size={}",
                inviteCode, radius, size);

        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));

        log.info("[PlaceService] Found meeting id={}, type={}", meeting.getId(), meeting.getType());

        RecommendedMidpoint recommendedMidpoint = recommendedMidpointRepository.findTopByMeetingOrderByRecommendedAtDesc(
                        meeting)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RECOMMENDED_MIDPOINT_NOT_FOUND));
        Midpoint midpoint = recommendedMidpoint.getMidpoint();

        int removedVotes = voteRepository.deleteByMeeting(meeting);
        log.info("[PlaceService] Cleared {} votes for meeting id={}", removedVotes, meeting.getId());

        long deleted = placeRepository.deleteByMeeting(meeting);
        log.info("[PlaceService] Cleared {} existing place candidates for meeting id={}",
                deleted, meeting.getId());

        double lat = midpoint.getLatitude().doubleValue();
        double lng = midpoint.getLongitude().doubleValue();
        log.info("[PlaceService] Midpoint lat={}, lng={}", lat, lng);

        Map<String, KakaoPlaceDoc> seen = new HashMap<>(); // 전역 dedup (place id)
        List<PlaceSectionDto> sections = new ArrayList<>();

        int pageLocal = pageFromSeq(refreshSeq, MAX_PAGE);
        if (meeting.getType() == MeetingType.SOCIAL) {
            log.info("[PlaceService] Processing SOCIAL meeting -> FOOD & FUN");
            for (PlaceSection sec : List.of(PlaceSection.FOOD, PlaceSection.FUN)) {
                List<KakaoPlaceDoc> bucket = new ArrayList<>();
                for (String code : SOCIAL_CODES.get(sec)) {
                    var docs = kakaoClient.searchCategory(code, lat, lng, radius, pageLocal, size);

                    if (docs.isEmpty() && pageLocal != 1) {
                        log.info("[PlaceService] Empty page={} -> fallback page=1 (sec={}, code={})",
                                pageLocal, sec, code);
                        pageLocal = 1;
                        docs = kakaoClient.searchCategory(code, lat, lng, radius, pageLocal, size);
                    }

                    log.info("[PlaceService] Retrieved {} places (sec={}, code={}, page={})",
                            docs.size(), sec, code, pageLocal);

                    for (var d : docs) {
                        if (seen.putIfAbsent(d.getId(), d) != null) {
                            continue;
                        }
                        // MeetingType의 허용 카테고리(있다면) 확인
                        if (!meeting.getType().getCategories()
                                .contains(PlaceCategory.fromKakao(d.getCategory_group_code()))) {
                            continue;
                        }
                        bucket.add(d);
                    }
                }
                List<PlaceResponseDto> items = placeSlotAssigner.assignAndBuild(sec, lat, lng, bucket);
                String seedJson = toSeedJsonForCategory(SOCIAL_CODES.get(sec), lat, lng, radius, pageLocal, size);
                for (PlaceResponseDto p : items) {
                    upsertCandidate(meeting, p.getSlotNo(), sec, "category", seedJson);
                }
                sections.add(new PlaceSectionDto(sec, sec.label, items));
            }
        } else if (meeting.getType() == MeetingType.PROJECT) {
            log.info("[PlaceService] Processing PROJECT meeting -> searchKeyword: {}",
                    STUDY_KEYWORD);
            var docs = kakaoClient.searchKeyword(STUDY_KEYWORD, lat, lng, Math.max(radius, 800), pageLocal, size);
            if (docs.size() < 5) {
                log.info("[PlaceService] page={} -> fallback to page=1", pageLocal);
                pageLocal = 1;
                docs = kakaoClient.searchKeyword(STUDY_KEYWORD, lat, lng, Math.max(radius, 800), pageLocal, size);
            }
            log.info("[PlaceService] Retrieved {} places for PROJECT", docs.size());

            List<KakaoPlaceDoc> bucket = docs.stream()
                    .filter(d -> seen.putIfAbsent(d.getId(), d) == null)
                    .toList();
            List<PlaceResponseDto> items = placeSlotAssigner.assignAndBuild(PlaceSection.STUDY, lat, lng, bucket);
            String seedJson = toSeedJsonForKeyword(lat, lng, Math.max(radius, 800), pageLocal, size);
            for (PlaceResponseDto p : items) {
                upsertCandidate(meeting, p.getSlotNo(), PlaceSection.STUDY, "keyword", seedJson);
            }
            sections.add(new PlaceSectionDto(PlaceSection.STUDY, PlaceSection.STUDY.label, items));
        }
        log.info("[PlaceService] Finished grouping -> total sections={}", sections.size());

        return new GroupedPlacesResponse(sections);
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
}
