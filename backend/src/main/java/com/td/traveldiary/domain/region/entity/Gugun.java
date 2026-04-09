package com.td.traveldiary.domain.region.entity;

import com.td.traveldiary.global.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Gugun extends BaseEntity {
    private long id;
    private long sidoId;
    private String name;
    private int code;
}
