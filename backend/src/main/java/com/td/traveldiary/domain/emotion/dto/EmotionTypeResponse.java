package com.td.traveldiary.domain.emotion.dto;

import com.td.traveldiary.domain.emotion.entity.EmotionType;

public record EmotionTypeResponse(
        long id,
        String name
) {
    public static EmotionTypeResponse from(EmotionType emotionType) {
        return new EmotionTypeResponse(emotionType.getId(), emotionType.getName());
    }
}
