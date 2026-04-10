package com.td.traveldiary.domain.content.controller;

import com.td.traveldiary.domain.content.dto.ContentTypeResponse;
import com.td.traveldiary.domain.content.service.ContentTypeService;
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

@WebMvcTest(ContentTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContentTypeService contentTypeService;

    @Test
    void getContentTypes_returns_200() throws Exception {
        when(contentTypeService.getContentTypes()).thenReturn(List.of(
                new ContentTypeResponse(1L, "여유로운")
        ));

        mockMvc.perform(get("/api/v1/content-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("여유로운"));
    }

}