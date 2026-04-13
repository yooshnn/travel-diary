package com.td.traveldiary.domain.attraction.entity;

import com.td.traveldiary.global.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Attraction extends BaseEntity {
    private Long id;
    private Long memberId;
    private Long sidoId;
    private Long gugunId;
    private Long contentTypeId;
    private Long emotionTypeId;     // nullable. AI 분석 이전 null
    private String name;
    private String photo;           // nullable.
    private Double latitude;
    private Double longitude;
    private String address;
    private String overview;
    private boolean isDeleted;

    @Builder
    public Attraction(Long id, Long memberId, Long sidoId, Long gugunId, Long contentTypeId, Long emotionTypeId,
                      String name, String photo, Double latitude, Double longitude,
                      String address, String overview, boolean isDeleted) {
        this.id = id;
        this.memberId = memberId;
        this.sidoId = sidoId;
        this.gugunId = gugunId;
        this.contentTypeId = contentTypeId;
        this.emotionTypeId = emotionTypeId;
        this.name = name;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.overview = overview;
        this.isDeleted = isDeleted;
    }
}
