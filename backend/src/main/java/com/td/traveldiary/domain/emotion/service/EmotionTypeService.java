package com.td.traveldiary.domain.emotion.service;

import com.td.traveldiary.domain.emotion.dto.EmotionTypeResponse;
import com.td.traveldiary.domain.emotion.entity.EmotionType;
import com.td.traveldiary.domain.emotion.repository.EmotionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmotionTypeService {

    private final EmotionTypeRepository emotionTypeRepository;

    public List<EmotionTypeResponse> getEmotionTypes() {
        List<EmotionType> emotionTypes = emotionTypeRepository.findAll();
        return emotionTypes.stream()
                .map(EmotionTypeResponse::from)
                .toList();
    }
}
