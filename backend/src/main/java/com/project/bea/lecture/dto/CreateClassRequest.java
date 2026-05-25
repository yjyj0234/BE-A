package com.project.bea.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassRequest {

    private Long creatorId;

    private String title;

    private String description;

    private Integer price;

    private Integer capacity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}