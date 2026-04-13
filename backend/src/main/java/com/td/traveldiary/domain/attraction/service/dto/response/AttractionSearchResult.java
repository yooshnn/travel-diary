package com.td.traveldiary.domain.attraction.service.dto.response;

public record AttractionSearchResult(
        Long attractionId,
        Long contentTypeId,
        Long emotionTypeId,
        String name,
        String photo,
        String address,
        Double latitude,
        Double longitude,
        Boolean isBookmarked,
        Integer bookmarkCount,
        Integer diaryCount
) {}
