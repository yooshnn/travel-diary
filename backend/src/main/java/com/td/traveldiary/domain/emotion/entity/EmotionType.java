package com.td.traveldiary.domain.emotion.entity;

import com.td.traveldiary.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionType extends BaseEntity {
    private long id;
    private String name;
}
