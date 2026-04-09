package com.td.traveldiary.domain.region.dto.response;

import com.td.traveldiary.domain.region.entity.Sido;

public record SidoResponse(
        long id,
        String name,
        int code
) {
    public static SidoResponse from(Sido sido) {
        return new SidoResponse(sido.getId(), sido.getName(), sido.getCode());
    }
}
