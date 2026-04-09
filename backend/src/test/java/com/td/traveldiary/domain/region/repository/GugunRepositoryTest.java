package com.td.traveldiary.domain.region.repository;

import com.td.traveldiary.domain.region.entity.Gugun;
import com.td.traveldiary.domain.region.entity.Sido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class GugunRepositoryTest {

    @Autowired
    private GugunRepository gugunRepository;

    @Test
    void findBySidoId_returns_non_empty_list() {
        List<Gugun> result = gugunRepository.findBySidoId(1L);
        assertThat(result).isNotEmpty();
    }

    @Test
    void findBySidoId_returns_empty_list_when_sido_not_found() {
        List<Gugun> result = gugunRepository.findBySidoId(-1L);
        assertThat(result).isEmpty();
    }

    @Test
    void findBySidoId_returns_sorted_by_id_asc() {
        List<Gugun> result = gugunRepository.findBySidoId(1L);
        assertThat(result)
                .extracting(Gugun::getId)
                .isSortedAccordingTo(Long::compareTo);
    }

}