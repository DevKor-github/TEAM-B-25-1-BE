package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.Vote;
import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.ParticipantRepository;
import com.ODG.ODG_back.repository.PlaceRepository;
import com.ODG.ODG_back.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final MeetingRepository meetingRepository;
    private final PlaceRepository placeRepository;
    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;

    public void vote(String inviteCode, VoteRequestDto voteRequestDTO) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        Place place = placeRepository.findById(voteRequestDTO.getPlaceId())
                .orElseThrow(() -> new RuntimeException("Place not found"));
        Participant participant = participantRepository.findById(voteRequestDTO.getParticipantId())
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        Optional<Vote> existingVote = voteRepository.findByMeetingAndPlaceAndParticipant(meeting, place, participant);

        if (existingVote.isEmpty()) {
            Vote newVote = new Vote(meeting, place, participant);
            voteRepository.save(newVote);
        }
        // toggle 형식
        else {
            if (existingVote.get().getVoteValue() == 1) {
                existingVote.get().setVoteValue(0);
            }
            else {
                existingVote.get().setVoteValue(1);
            }
        }
    }

    public List<VoteResultDto> getVoteResults(String inviteCode) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        return voteRepository.findVoteCountsByMeeting(meeting);
    }
}
