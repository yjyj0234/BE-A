package com.project.bea.lecture.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateClassRequest {

    private Long creatorId;

    private String title;

    private String description;

    private Integer price;

    private Integer capacity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}