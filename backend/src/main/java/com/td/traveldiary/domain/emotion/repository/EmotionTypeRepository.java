package com.td.traveldiary.domain.emotion.repository;

import com.td.traveldiary.domain.emotion.entity.EmotionType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmotionTypeRepository {
    List<EmotionType> findAll();
}
