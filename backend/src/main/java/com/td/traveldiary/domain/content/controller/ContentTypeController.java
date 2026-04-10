package com.td.traveldiary.domain.content.controller;

import com.td.traveldiary.domain.content.dto.ContentTypeResponse;
import com.td.traveldiary.domain.content.service.ContentTypeService;
import com.td.traveldiary.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content-type")
@RequiredArgsConstructor
public class ContentTypeController {

    private final ContentTypeService contentTypeService;

    @GetMapping
    public ApiResponse<List<ContentTypeResponse>> getContentTypes() {
        return ApiResponse.onSuccess(contentTypeService.getContentTypes());
    }
}