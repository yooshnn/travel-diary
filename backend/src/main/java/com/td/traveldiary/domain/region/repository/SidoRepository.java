package com.td.traveldiary.domain.region.repository;

import com.td.traveldiary.domain.region.entity.Sido;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SidoRepository {
    List<Sido> findAll();
}
