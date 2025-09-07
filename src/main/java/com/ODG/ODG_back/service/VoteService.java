package com.ODG.ODG_back.service;

import com.ODG.ODG_back.domain.Meeting;
import com.ODG.ODG_back.domain.Participant;
import com.ODG.ODG_back.domain.Place;
import com.ODG.ODG_back.domain.Vote;
import com.ODG.ODG_back.domain.enums.PlaceCategory;
import com.ODG.ODG_back.dto.place.SeedParams;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.dto.vote.request.VoteRequestDto;
import com.ODG.ODG_back.dto.vote.response.VoteResultDto;
import com.ODG.ODG_back.exception.ErrorCode;
import com.ODG.ODG_back.exception.custom.NotFoundException;
import com.ODG.ODG_back.external.kakao.KakaoLocalClient;
import com.ODG.ODG_back.external.kakao.KakaoLocalClient.KakaoPlaceDoc;
import com.ODG.ODG_back.repository.MeetingRepository;
import com.ODG.ODG_back.repository.ParticipantRepository;
import com.ODG.ODG_back.repository.PlaceRepository;
import com.ODG.ODG_back.repository.VoteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final MeetingRepository meetingRepository;
    private final PlaceRepository placeRepository;
    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;

    private final KakaoLocalClient kakaoLocalClient;
    private final ObjectMapper objectMapper;

    // --- Lightweight response DTOs for instant UI patching ---
    public record VotePatchDto(Integer slotNo, boolean votedByMe) {}
    public record VoteInstantResponse(List<Integer> myVoteSlotNos, List<VotePatchDto> patches) {}

    public VoteInstantResponse vote(String inviteCode, String userId, VoteRequestDto voteRequestDto) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        Participant participant = participantRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND));

        Integer slotNo = Optional.ofNullable(voteRequestDto.getSlotNo())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));

        // 슬롯 존재 검증
        placeRepository.findByMeetingAndSlotNo(meeting, slotNo)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));

        // 토글 처리
        Optional<Vote> existingVote = voteRepository.findByMeetingAndSlotNoAndParticipant(meeting, slotNo, participant);
        boolean nowVoted;
        if (existingVote.isEmpty()) {
            voteRepository.save(new Vote(meeting, slotNo, participant));
            nowVoted = true;
            log.info("[vote] VOTED  slotNo={} (participantId={})", slotNo, participant.getId());
        } else {
            voteRepository.delete(existingVote.get());
            nowVoted = false;
            log.info("[vote] UNVOTED slotNo={} (participantId={})", slotNo, participant.getId());
        }

        // 내 현재 전체 투표 슬롯들 재계산 (동기화용)
        List<Integer> myVoteSlotNos = voteRepository.findAllByMeetingAndParticipant(meeting, participant)
                .stream()
                .map(Vote::getSlotNo)
                .toList();

        // 이번 요청으로 바뀐 것만 패치로 반환
        List<VotePatchDto> patches = List.of(new VotePatchDto(slotNo, nowVoted));
        return new VoteInstantResponse(myVoteSlotNos, patches);
    }


    public List<PlaceResponseDto> getVoteResults(String inviteCode) {
        Meeting meeting = meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND));
        log.info("Fetched Meeting with inviteCode: {}, meetingId: {}", inviteCode, meeting.getId());

        // 1) slotNo 별 투표수 집계
        List<VoteResultDto> results = voteRepository.findVoteCountsByMeeting(meeting);
        log.info("Retrieved vote results count: {}", (results == null ? 0 : results.size()));

        if (results == null || results.isEmpty()) {
            throw new NotFoundException(ErrorCode.VOTE_NOT_FOUND);
        }

        List<PlaceResponseDto> finalPlaces = new ArrayList<>();

        for (VoteResultDto r : results) {
            log.info("Processing VoteResultDto with slotNo: {}, voteCount: {}", r.getSlotNo(), r.getVoteCount());
            if (r.getVoteCount() <= 0) break; // 집계가 내림차순이면 0 이하면 이후도 0
            int slotNo = r.getSlotNo();

            // 2) slotNo -> Place seed 매핑 (없으면 건너뜀)
            Place seed = placeRepository.findByMeetingAndSlotNo(meeting, slotNo).orElse(null);
            if (seed == null) continue;

            // 3) seedParams 역직렬화 (파싱 실패 시 건너뜀)
            SeedParams params = parseSeedParams(seed);
            if (params == null) continue;

            // 4) seed로 재조회
            List<KakaoLocalClient.KakaoPlaceDoc> docs = fetchDocsBySeed(params);
            if (docs == null || docs.isEmpty()) continue;

            // 5) 선택 규칙
            KakaoPlaceDoc chosen = chooseDoc(params, docs, slotNo);
            if (chosen == null) continue;

            // 6) DTO로 추가 (저장 X)
            PlaceResponseDto dto = toDto(chosen, slotNo, r.getVoteCount());
            finalPlaces.add(dto);
        }

        if (finalPlaces.isEmpty()) {
            throw new NotFoundException(ErrorCode.PLACE_NOT_FOUND);
        }
        return finalPlaces;
    }


    private SeedParams parseSeedParams(Place seed) {
        try {
            SeedParams params = objectMapper.readValue(seed.getSeedParamsJson(), SeedParams.class);
            log.info("Parsed SeedParams: {}", params);
            return params;
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse SeedParams for place id={}: {}", seed.getId(), e.getMessage());
            return null;
        }
    }

    private List<KakaoPlaceDoc> fetchDocsBySeed(SeedParams params) {
        if (params == null) return null;
        List<KakaoPlaceDoc> docs;

        int page = (params.getPage() > 0) ? params.getPage() : 1;
        int size = (params.getSize() > 0) ? params.getSize() : 15;

        if ("category".equalsIgnoreCase(params.getType())) {
            List<String> codes = (params.getCodes() != null) ? params.getCodes() : List.of();
            if (codes.isEmpty()) return null;

            if (codes.size() == 1) {
                String code = codes.get(0);
                docs = kakaoLocalClient.searchCategory(code, params.getLat(), params.getLng(),
                        params.getRadius(), page, size);
            } else {
                // Concatenate per-code results in order: code[0] then code[1]
                List<KakaoPlaceDoc> merged = new ArrayList<>(size * codes.size());
                for (String code : codes) {
                    List<KakaoPlaceDoc> one = kakaoLocalClient.searchCategory(code, params.getLat(), params.getLng(),
                            params.getRadius(), page, size);
                    if (one != null && !one.isEmpty()) merged.addAll(one);
                }
                docs = merged;
            }
        } else {
            docs = kakaoLocalClient.searchKeyword(params.getKeyword(), params.getLat(), params.getLng(),
                    params.getRadius(), page, size);
        }

        if (docs == null) return null;

        log.info("KakaoLocalClient returned {} docs after merge+distance-sort (page={}, size={})", docs.size(), page, size);
        return docs;
    }

    private KakaoPlaceDoc chooseDoc(SeedParams params, List<KakaoPlaceDoc> docs, int slotNo) {
        if (params == null || docs == null || docs.isEmpty()) return null;

        int size = (params.getSize() > 0) ? params.getSize() : 15;
        int codes = (params.getCodes() != null && !params.getCodes().isEmpty()) ? params.getCodes().size() : 1;

        int slotOffset = Math.floorMod(slotNo, 1000);
        int idx = slotOffset % (size * codes);
        idx = Math.max(0, Math.min(idx, docs.size() - 1));

        KakaoPlaceDoc chosen = docs.get(idx);
        log.info("Choosing doc for slotNo={} (slotOffset={}, idx={}), chosen placeId={}, name={}",
                slotNo, slotOffset, idx, chosen.getId(), chosen.getPlace_name());
        return chosen;
    }

    private PlaceResponseDto toDto(KakaoPlaceDoc chosen, int slotNo, int voteCount) {
        if (chosen == null) return null;
        return new PlaceResponseDto(
                chosen.getId(),
                chosen.getPlace_name(),
                PlaceCategory.fromKakao(chosen.getCategory_group_code()),
                BigDecimal.valueOf(chosen.getY()),
                BigDecimal.valueOf(chosen.getX()),
                chosen.getAddress_name(),
                slotNo,
                chosen.getPlace_url(),
                voteCount
        );
    }
}
