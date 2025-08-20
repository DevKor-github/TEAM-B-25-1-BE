package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import com.ODG.ODG_back.domain.enums.MeetingType;
import com.ODG.ODG_back.domain.enums.PlaceCategory;
import com.ODG.ODG_back.dto.place.response.GroupedPlacesResponse;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.dto.place.response.PlaceSection;
import com.ODG.ODG_back.dto.place.response.PlaceSectionDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.external.kakao.KakaoLocalClient;
import com.ODG.ODG_back.external.kakao.KakaoLocalClient.KakaoPlaceDoc;
import com.ODG.ODG_back.mapper.PlaceMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.PlaceRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
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

    private static final Map<PlaceSection, List<String>> SOCIAL_CODES = Map.of(
        PlaceSection.FOOD, List.of("FD6", "CE7"),
        PlaceSection.FUN, List.of("AT4", "CT1")
    );
    private static final String STUDY_KEYWORD = "스터디카페";

    public GroupedPlacesResponse getPlacesByMidpointGrouped(String inviteCode, int radius, int page, int size) {
        log.info("[PlaceService] getPlacesByMidpointGrouped inviteCode={}, radius={}, page={}, size={}",
            inviteCode, radius, page, size);

        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));

        log.info("[PlaceService] Found meeting id={}, type={}", meeting.getId(), meeting.getType());


        RecommendedMidpoint recommendedMidpoint = recommendedMidpointRepository.findTopByMeetingOrderByRecommendedAtDesc(meeting)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RECOMMENDED_MIDPOINT_NOT_FOUND));
        Midpoint midpoint = recommendedMidpoint.getMidpoint();

        double lat = midpoint.getLatitude().doubleValue();
        double lng = midpoint.getLongitude().doubleValue();
        log.info("[PlaceService] Midpoint lat={}, lng={}", lat, lng);


        Map<String, KakaoPlaceDoc> seen = new HashMap<>(); // 전역 dedup (place id)
        List<PlaceSectionDto> sections = new ArrayList<>();

        if (meeting.getType() == MeetingType.SOCIAL) {
            log.info("[PlaceService] Processing SOCIAL meeting -> FOOD & FUN");
            for (PlaceSection sec : List.of(PlaceSection.FOOD, PlaceSection.FUN)) {
                List<PlaceResponseDto> items = new ArrayList<>();
                for (String code : SOCIAL_CODES.get(sec)) {
                    var docs = kakaoClient.searchCategory(code, lat, lng, radius, page, size);
                    log.info("[PlaceService] Retrieved {} places for section={} code={}", docs.size(), sec, code);

                    for (var d : docs) {
                        if (seen.putIfAbsent(d.getId(), d) != null) continue;
                        // MeetingType의 허용 카테고리(있다면) 확인
                        if (!meeting.getType().getCategories()
                            .contains(PlaceCategory.fromKakao(d.getCategory_group_code()))) continue;
                        items.add(toDto(d));
                    }
                }
                items.sort(Comparator.comparingDouble(p -> haversine(lat, lng,
                    p.getLatitude().doubleValue(), p.getLongitude().doubleValue())));
                sections.add(new PlaceSectionDto(sec, sec.label, items));
            }
        } else if (meeting.getType() == MeetingType.PROJECT) {
            log.info("[PlaceService] Processing PROJECT meeting -> searchKeyword: {}", STUDY_KEYWORD);
            var docs = kakaoClient.searchKeyword(STUDY_KEYWORD, lat, lng, Math.max(radius, 800), page, size);
            log.info("[PlaceService] Retrieved {} places for PROJECT", docs.size());

            List<PlaceResponseDto> items = docs.stream()
                .filter(d -> seen.putIfAbsent(d.getId(), d) == null)
                .map(this::toDto)
                .sorted(Comparator.comparingDouble(p -> haversine(lat, lng,
                    p.getLatitude().doubleValue(), p.getLongitude().doubleValue())))
                .toList();
            sections.add(new PlaceSectionDto(PlaceSection.STUDY, PlaceSection.STUDY.label, items));
        } else {
            // 기타 타입은 SOCIAL과 동일 처리
            for (PlaceSection sec : List.of(PlaceSection.FOOD, PlaceSection.FUN)) {
                List<PlaceResponseDto> items = new ArrayList<>();
                for (String code : SOCIAL_CODES.get(sec)) {
                    var docs = kakaoClient.searchCategory(code, lat, lng, radius, page, size);
                    for (var d : docs) {
                        if (seen.putIfAbsent(d.getId(), d) != null) continue;
                        items.add(toDto(d));
                    }
                }
                items.sort(Comparator.comparingDouble(p -> haversine(lat, lng,
                    p.getLatitude().doubleValue(), p.getLongitude().doubleValue())));
                sections.add(new PlaceSectionDto(sec, sec.label, items));
            }
        }
        log.info("[PlaceService] Finished grouping -> total sections={}", sections.size());

        return new GroupedPlacesResponse(sections);
    }

    private PlaceResponseDto toDto(KakaoPlaceDoc d) {
        return new PlaceResponseDto(
            d.getId(),
            d.getPlace_name(),
            PlaceCategory.fromKakao(d.getCategory_group_code()),
            BigDecimal.valueOf(d.getY()),
            BigDecimal.valueOf(d.getX()),
            d.getAddress_name()
        );
    }

    private static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double R=6371000d, dLat=Math.toRadians(lat2-lat1), dLng=Math.toRadians(lng2-lng1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+
            Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                Math.sin(dLng/2)*Math.sin(dLng/2);
        return 2*R*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

}
