package com.td.traveldiary.domain.emotion.controller;

import com.td.traveldiary.domain.emotion.controller.EmotionTypeController;
import com.td.traveldiary.domain.emotion.dto.EmotionTypeResponse;
import com.td.traveldiary.domain.emotion.service.EmotionTypeService;
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

@WebMvcTest(EmotionTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmotionTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmotionTypeService emotionTypeService;

    @Test
    void getEmotionTypes_returns_200() throws Exception {
        when(emotionTypeService.getEmotionTypes()).thenReturn(List.of(
                new EmotionTypeResponse(1L, "여유로운")
        ));

        mockMvc.perform(get("/api/v1/emotion-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("여유로운"));
    }

}