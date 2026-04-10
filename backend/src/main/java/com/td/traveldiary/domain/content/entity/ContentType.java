package com.td.traveldiary.domain.content.entity;

import com.td.traveldiary.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentType extends BaseEntity {
    private long id;
    private String name;
}
