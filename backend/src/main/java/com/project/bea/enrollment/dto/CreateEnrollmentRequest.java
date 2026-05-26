package com.project.bea.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * file: CreateEnrollmentRequest.java
 * author: 손현정
 * description: 수강 신청 요청 데이터를 전달하는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentRequest {

    private Long classId;

    private Long studentId;
}