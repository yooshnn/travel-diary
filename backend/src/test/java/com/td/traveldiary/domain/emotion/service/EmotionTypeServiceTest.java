package com.td.traveldiary.domain.emotion.service;

import com.td.traveldiary.domain.emotion.dto.EmotionTypeResponse;
import com.td.traveldiary.domain.emotion.entity.EmotionType;
import com.td.traveldiary.domain.emotion.repository.EmotionTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmotionTypeServiceTest {

    @InjectMocks
    private EmotionTypeService emotionTypeService;

    @Mock
    private EmotionTypeRepository emotionTypeRepository;

    @Test
    void getContentTypes_returns_contentType_list() {
        EmotionType emotionType = new EmotionType(1L, "여유로운");
        when(emotionTypeRepository.findAll()).thenReturn(List.of(emotionType));

        List<EmotionTypeResponse> result = emotionTypeService.getEmotionTypes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("여유로운");
    }

}