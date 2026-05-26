package com.project.bea.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * file: CreateClassRequest.java
 * author: 손현정
 * description: 강의 등록 요청 데이터를 전달하는 DTO
 */
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