package com.td.traveldiary.domain.region.controller;

import com.td.traveldiary.domain.region.dto.GugunResponse;
import com.td.traveldiary.domain.region.dto.SidoResponse;
import com.td.traveldiary.domain.region.exception.SidoNotFoundException;
import com.td.traveldiary.domain.region.service.RegionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegionService regionService;

    @Test
    void getSidos_returns_200() throws Exception {
        when(regionService.getSidos()).thenReturn(List.of(
                new SidoResponse(1L, "서울", 1)
        ));

        mockMvc.perform(get("/api/v1/region/sido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("서울"));
    }

    @Test
    void getGuguns_returns_200() throws Exception {
        when(regionService.getGuguns(1L)).thenReturn(List.of(
                new GugunResponse(235L, "강남구", 1)
        ));

        mockMvc.perform(get("/api/v1/region/sido/1/gugun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("강남구"));
    }

    @Test
    void getGuguns_returns_404_when_sido_not_found() throws Exception {
        when(regionService.getGuguns(-1L)).thenThrow(new SidoNotFoundException());

        mockMvc.perform(get("/api/v1/region/sido/-1/gugun"))
                .andExpect(status().isNotFound());
    }
}