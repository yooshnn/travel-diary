package com.td.traveldiary.domain.content.service;

import com.td.traveldiary.domain.content.dto.ContentTypeResponse;
import com.td.traveldiary.domain.content.entity.ContentType;
import com.td.traveldiary.domain.content.repository.ContentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentTypeService {

    private final ContentTypeRepository contentTypeRepository;

    public List<ContentTypeResponse> getContentTypes() {
        List<ContentType> contentTypes = contentTypeRepository.findAll();
        return contentTypes.stream()
                .map(contentType -> ContentTypeResponse.from(contentType))
                .toList();
    }

}
