package com.ODG.ODG_back.controller;

import com.ODG.ODG_back.dto.midpoint.response.MidpointResponseDto;
import com.ODG.ODG_back.service.MidpointService;
import com.ODG.ODG_back.strategy.MidpointStrategyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MidpointController.class)
@AutoConfigureMockMvc(addFilters = false)
class MidpointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MidpointService midpointService;

    @Test
    @DisplayName("중간지점 계산 API - 정상 호출")
    void getMidpoints() throws Exception {
        // given
        String inviteCode = "test123";
        MidpointStrategyType strategyType = MidpointStrategyType.TIME_MATRIX;

        MidpointResponseDto mockResponse = new MidpointResponseDto(
                1L, "강남역", new BigDecimal("37.4979"), new BigDecimal("127.0276")
        );
        given(midpointService.getRecommendedMidpoints(inviteCode, strategyType)).willReturn(
                mockResponse);

        // when & then
        mockMvc.perform(get("/meetings/{inviteCode}/midpoint", inviteCode)
                        .param("strategyType", strategyType.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("강남역"))
                .andExpect(jsonPath("$.latitude").value("37.4979"))
                .andExpect(jsonPath("$.longitude").value("127.0276"));
    }
}