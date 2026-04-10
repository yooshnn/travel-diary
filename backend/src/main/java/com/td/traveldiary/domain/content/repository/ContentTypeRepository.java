package com.td.traveldiary.domain.content.repository;

import com.td.traveldiary.domain.content.entity.ContentType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContentTypeRepository {
    List<ContentType> findAll();
}
