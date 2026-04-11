package com.td.traveldiary.domain.member.repository;

import com.td.traveldiary.domain.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberRepository {
    Optional<Member> findByProviderId(@Param("providerId") String providerId);
    Optional<Member> findById(Long id);
    void save(Member member);
    void update(Member member);
}
