package com.project.bea.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * file: ClassResponse.java
 * author: 손현정
 * description: 강의 응답 데이터를 전달하는 DTO
 */
@Getter
@Builder
public class ClassResponse {

    private Long id;

    private String title;

    private String description;

    private Integer price;

    private Integer capacity;

    private Integer currentEnrollmentCount;

    private String status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}