package com.td.traveldiary.domain.region.service;

import com.td.traveldiary.domain.region.dto.GugunResponse;
import com.td.traveldiary.domain.region.dto.SidoResponse;
import com.td.traveldiary.domain.region.entity.Gugun;
import com.td.traveldiary.domain.region.entity.Sido;
import com.td.traveldiary.domain.region.exception.SidoNotFoundException;
import com.td.traveldiary.domain.region.repository.GugunRepository;
import com.td.traveldiary.domain.region.repository.SidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

// Mockito 기능을 활성화 해, @Mock, @InjectMocks를 사용할 수 있게 한다.
@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    // 테스트 대상
    @InjectMocks
    private RegionService regionService;

    // 실제 DB에 접근하지 않는 Mock Repository
    @Mock
    private SidoRepository sidoRepository;

    @Mock
    private GugunRepository gugunRepository;

    @Test
    void getSidos_returns_sido_list() {
        Sido sido = new Sido(1L, "서울", 1);
        when(sidoRepository.findAll()).thenReturn(List.of(sido));

        List<SidoResponse> result = regionService.getSidos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("서울");
    }

    @Test
    void getGuguns_returns_gugun_list() {
        Gugun gugun = new Gugun(235L, 1L, "강남구", 1);
        when(gugunRepository.findBySidoId(1L)).thenReturn(List.of(gugun));

        List<GugunResponse> result = regionService.getGuguns(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("강남구");
    }

    @Test
    void getGuguns_throws_SidoNotFoundException_when_sido_not_found() {
        when(gugunRepository.findBySidoId(-1L)).thenReturn(List.of());

        assertThatThrownBy(() -> regionService.getGuguns(-1L))
                .isInstanceOf(SidoNotFoundException.class);
    }
}