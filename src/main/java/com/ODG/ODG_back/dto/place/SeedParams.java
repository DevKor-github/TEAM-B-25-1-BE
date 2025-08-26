package com.ODG.ODG_back.dto.place;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedParams {
    private String type;
    private List<String> codes;
    private String keyword;
    private double lat;
    private double lng;
    private int radius;
    private int page;
    private int size;
    private String sort;
    private Integer pickIndex;
}
