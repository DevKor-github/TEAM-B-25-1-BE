package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.Vote;
import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.ParticipantRepository;
import com.ODG.ODG_back.repository.PlaceRepository;
import com.ODG.ODG_back.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final MeetingRepository meetingRepository;
    private final PlaceRepository placeRepository;
    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;

    public void vote(String inviteCode, String userId, VoteRequestDto voteRequestDto) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        Place place = placeRepository.findById(voteRequestDto.getPlaceId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));
        Participant participant = participantRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));

        Optional<Vote> existingVote = voteRepository.findByMeetingAndPlaceAndParticipant(meeting, place, participant);

        if (existingVote.isEmpty()) {
            Vote newVote = new Vote(meeting, place, participant);
            voteRepository.save(newVote);
        } else {
            voteRepository.delete(existingVote.get());
        }
    }

    public List<VoteResultDto> getVoteResults(String inviteCode) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));

        return voteRepository.findVoteCountsByMeeting(meeting);
    }
}
