package com.project.bea.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClassResponse {

    private Long id;

    private String title;

    private String description;

    private Integer price;

    private Integer capacity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}