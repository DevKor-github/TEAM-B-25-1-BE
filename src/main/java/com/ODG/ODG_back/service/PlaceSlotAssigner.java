package com.ODG.ODG_back.service;


import com.ODG.ODG_back.domain.enums.PlaceCategory;
import com.ODG.ODG_back.dto.place.response.PlaceResponseDto;
import com.ODG.ODG_back.dto.place.response.PlaceSection;
import com.ODG.ODG_back.external.kakao.KakaoLocalClient.KakaoPlaceDoc;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceSlotAssigner {

    private static final Map<PlaceSection, Integer> SECTION_BASE = Map.of(
            PlaceSection.FOOD, 1000,
            PlaceSection.FUN, 2000,
            PlaceSection.STUDY, 3000
    );

    public List<PlaceResponseDto> assignAndBuild(
            PlaceSection section, double centerLat, double centerLng,
            List<KakaoPlaceDoc> docs, int startSlotNo) {

        List<PlaceResponseDto> items = new ArrayList<>(docs.size());

        for (int i = 0; i < docs.size(); i++) {
            KakaoPlaceDoc doc = docs.get(i);
            items.add(new PlaceResponseDto(
                    doc.getId(),
                    doc.getPlace_name(),
                    PlaceCategory.fromKakao(doc.getCategory_group_code()),
                    BigDecimal.valueOf(doc.getY()),
                    BigDecimal.valueOf(doc.getX()),
                    doc.getAddress_name(),
                    startSlotNo + i, // ✅ 주어진 시작 슬롯부터 연속 할당
                    doc.getPlace_url(),
                    false,
                    0
            ));
        }
        return items;
    }

}
