package com.td.traveldiary.domain.emotion.controller;

import com.td.traveldiary.domain.emotion.dto.EmotionTypeResponse;
import com.td.traveldiary.domain.emotion.service.EmotionTypeService;
import com.td.traveldiary.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emotion-type")
@RequiredArgsConstructor
public class EmotionTypeController {

    private final EmotionTypeService emotionTypeService;

    @GetMapping
    public ApiResponse<List<EmotionTypeResponse>> getEmotionTypes() {
        return ApiResponse.onSuccess(emotionTypeService.getEmotionTypes());
    }
}
