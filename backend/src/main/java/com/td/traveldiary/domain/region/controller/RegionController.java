package com.td.traveldiary.domain.region.controller;

import com.td.traveldiary.domain.region.dto.GugunResponse;
import com.td.traveldiary.domain.region.dto.SidoResponse;
import com.td.traveldiary.domain.region.service.RegionService;
import com.td.traveldiary.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/region")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/sido")
    public ApiResponse<List<SidoResponse>> getSidos() {
        return ApiResponse.onSuccess(regionService.getSidos());
    }

    @GetMapping("/sido/{sidoId}/gugun")
    public ApiResponse<List<GugunResponse>> getGuguns(@PathVariable Long sidoId) {
        return ApiResponse.onSuccess(regionService.getGuguns(sidoId));
    }
}
