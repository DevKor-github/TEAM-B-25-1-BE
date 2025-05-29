package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import com.ODG.ODG_back.domain.enums.MeetingType;
import com.ODG.ODG_back.domain.enums.PlaceCategory;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.mapper.PlaceMapper;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.PlaceRepository;
import com.ODG.ODG_back.repository.RecommendedMidpointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final MeetingRepository meetingRepository;
    private final RecommendedMidpointRepository recommendedMidpointRepository;
    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    public List<PlaceResponseDto> getPlacesByMidpoint(String inviteCode) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode);

        RecommendedMidpoint recommendedMidpoint = recommendedMidpointRepository.findByMeeting(meeting);
        Midpoint midpoint = recommendedMidpoint.getMidpoint();

        List<Place> places = placeRepository.findByMidpoint(midpoint);

        return switch (meeting.getType()) {
            case SOCIAL -> filterSocialPlaces(places);
            case PROJECT -> filterProjectPlaces(places);
        };
    }
    private List<PlaceResponseDto> filterSocialPlaces(List<Place> places) {
        return places.stream()
                .filter(p -> p.getCategory() == PlaceCategory.RESTAURANT
                        || p.getCategory() == PlaceCategory.CAFE
                        || p.getCategory() == PlaceCategory.ENTERTAINMENT
                )
                .map(placeMapper::toDto)
                .toList();
    }

    private List<PlaceResponseDto> filterProjectPlaces(List<Place> places) {
        return places.stream()
                .filter(p -> p.getCategory() == PlaceCategory.LOUNGE
                        || p.getCategory() == PlaceCategory.STUDY_CAFE
                )
                .map(placeMapper::toDto)
                .toList();
    }
}
