package com.td.traveldiary.domain.content.service;

import com.td.traveldiary.domain.content.dto.ContentTypeResponse;
import com.td.traveldiary.domain.content.entity.ContentType;
import com.td.traveldiary.domain.content.repository.ContentTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentTypeServiceTest {

    @InjectMocks
    private ContentTypeService contentTypeService;

    @Mock
    private ContentTypeRepository contentTypeRepository;

    @Test
    void getContentTypes_returns_contentType_list() {
        ContentType contentType = new ContentType(12L, "관광지");
        when(contentTypeRepository.findAll()).thenReturn(List.of(contentType));

        List<ContentTypeResponse> result = contentTypeService.getContentTypes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("관광지");
    }

}