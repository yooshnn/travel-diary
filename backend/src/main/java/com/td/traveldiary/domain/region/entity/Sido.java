package com.td.traveldiary.domain.region.entity;

import com.td.traveldiary.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Sido extends BaseEntity {
    private long id;
    private String name;
    private int code;
}
