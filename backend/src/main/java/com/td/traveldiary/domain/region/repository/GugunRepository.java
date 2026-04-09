package com.td.traveldiary.domain.region.repository;

import com.td.traveldiary.domain.region.entity.Gugun;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GugunRepository {
    List<Gugun> findBySidoId(Long sidoId);
}
