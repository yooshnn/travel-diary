package com.td.traveldiary.domain.region.service;

import com.td.traveldiary.domain.region.dto.GugunResponse;
import com.td.traveldiary.domain.region.dto.SidoResponse;
import com.td.traveldiary.domain.region.entity.Gugun;
import com.td.traveldiary.domain.region.entity.Sido;
import com.td.traveldiary.domain.region.exception.SidoNotFoundException;
import com.td.traveldiary.domain.region.repository.GugunRepository;
import com.td.traveldiary.domain.region.repository.SidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;

    public List<SidoResponse> getSidos() {
        List<Sido> sidos = sidoRepository.findAll();
        return sidos.stream()
                .map(sido -> SidoResponse.from(sido))
                .toList();
    }

    public List<GugunResponse> getGuguns(Long sidoId) {
        List<Gugun> guguns = gugunRepository.findBySidoId(sidoId);

        // 본 프로젝트에서는 sido 밑에 gugun이 없는 경우는 없다고 가정한다.
        if (guguns.isEmpty()) {
            throw new SidoNotFoundException();
        }

        return guguns.stream()
                .map(gugun -> GugunResponse.from(gugun))
                .toList();
    }
}
