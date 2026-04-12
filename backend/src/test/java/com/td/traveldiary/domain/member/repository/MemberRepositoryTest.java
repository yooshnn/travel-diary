package com.td.traveldiary.domain.member.repository;

import com.td.traveldiary.domain.member.entity.Member;
import com.td.traveldiary.domain.member.entity.Provider;
import com.td.traveldiary.domain.member.entity.Role;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member buildMember(String name) {
        return Member.builder()
                .name(name)
                .provider(Provider.GOOGLE)
                .providerId(UUID.randomUUID().toString()) // 중복 방지
                .role(Role.USER)
                .profileImageUrl(null)
                .isDeleted(false)
                .build();
    }

    @Test
    void save_and_findById_returns_member() {
        Member member = buildMember("홍길동");
        memberRepository.save(member);

        Member found = memberRepository.findById(member.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("홍길동");
        assertThat(found.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void findByProviderId_returns_member() {
        Member member = buildMember("홍길동");
        memberRepository.save(member);

        Member found = memberRepository.findByProviderId(member.getProviderId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("홍길동");
    }

    @Test
    void findById_returns_empty_when_not_exists() {
        Optional<Member> result = memberRepository.findById(-1L);
        assertThat(result).isEmpty();
    }

    @Test
    void update_reflects_changes() {
        Member member = buildMember("홍길동");
        memberRepository.save(member);

        member.updateName("새이름");
        member.updateProfileImageUrl("http://localhost:8080/uploads/new.jpg");
        memberRepository.update(member);

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("새이름");
        assertThat(updated.getProfileImageUrl()).isEqualTo("http://localhost:8080/uploads/new.jpg");
    }
}