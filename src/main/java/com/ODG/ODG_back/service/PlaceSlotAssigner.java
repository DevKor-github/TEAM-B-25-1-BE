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
            PlaceSection section, double cneterLat, double centerLng, List<KakaoPlaceDoc> docs) {
        List<KakaoPlaceDoc> sorted = docs.stream()
                .sorted(Comparator.comparingDouble(d -> haversine(cneterLat, centerLng, d.getY(), d.getX())))
                .toList();

        List<PlaceResponseDto> items = new ArrayList<>(sorted.size());
        int base = SECTION_BASE.getOrDefault(section, 9000);
        for (int i = 0; i < sorted.size(); i++) {
            KakaoPlaceDoc doc = sorted.get(i);
            items.add(new PlaceResponseDto(
                    doc.getId(),
                    doc.getPlace_name(),
                    PlaceCategory.fromKakao(doc.getCategory_group_code()),
                    BigDecimal.valueOf(doc.getY()),
                    BigDecimal.valueOf(doc.getX()),
                    doc.getAddress_name(),
                    base + i,
                    doc.place_url
            ));
        }
        return items;
    }

    static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double R=6371000d, dLat=Math.toRadians(lat2-lat1), dLng=Math.toRadians(lng2-lng1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+
                Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                        Math.sin(dLng/2)*Math.sin(dLng/2);
        return 2*R*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

}
