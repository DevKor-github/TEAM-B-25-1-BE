package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Midpoint;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.RecommendedMidpoint;
import com.ODG.ODG_back.domain.enums.MeetingType;
import com.ODG.ODG_back.domain.enums.PlaceCategory;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
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
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));

        RecommendedMidpoint recommendedMidpoint = recommendedMidpointRepository.findByMeeting(meeting)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RECOMMENDED_MIDPOINT_NOT_FOUND));
        Midpoint midpoint = recommendedMidpoint.getMidpoint();

        List<Place> places = placeRepository.findByMidpoints(midpoint);

        return filterPlaces(places, meeting.getType());
    }

    private List<PlaceResponseDto> filterPlaces(List<Place> places, MeetingType type) {
        List<PlaceCategory> allowed = type.getCategories();

        return places.stream()
                .filter(p -> allowed.contains(p.getCategory()))
                .map(placeMapper::toDto)
                .toList();
    }
}
