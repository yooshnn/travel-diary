package com.td.traveldiary.global.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
