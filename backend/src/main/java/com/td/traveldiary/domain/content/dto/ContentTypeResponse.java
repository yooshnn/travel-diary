package com.td.traveldiary.domain.content.dto;

import com.td.traveldiary.domain.content.entity.ContentType;

public record ContentTypeResponse (
    long id,
    String name
) {
    public static ContentTypeResponse from(ContentType contentType) {
        return new ContentTypeResponse(contentType.getId(), contentType.getName());
    }
}
