package com.td.traveldiary.domain.region.dto.response;

import com.td.traveldiary.domain.region.entity.Gugun;

public record GugunResponse(
        long id,
        String name,
        int code
) {
    public static GugunResponse from(Gugun gugun) {
        return new GugunResponse(gugun.getId(), gugun.getName(), gugun.getCode());
    }
}
