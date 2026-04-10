package com.td.traveldiary.domain.region.repository;

import com.td.traveldiary.domain.region.entity.Sido;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SidoRepositoryTest {

    @Autowired
    private SidoRepository sidoRepository;

    @Test
    void findAll_returns_non_empty_list() {
        List<Sido> result = sidoRepository.findAll();
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAll_returns_sorted_by_id_asc() {
        List<Sido> result = sidoRepository.findAll();
        assertThat(result)
                .extracting(Sido::getId)
                .isSortedAccordingTo(Long::compareTo);
    }

}