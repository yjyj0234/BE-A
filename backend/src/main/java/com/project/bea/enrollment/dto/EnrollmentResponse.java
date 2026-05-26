package com.project.bea.enrollment.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * file: EnrollmentResponse.java
 * author: 손현정
 * description: 수강 신청 응답 데이터를 전달하는 DTO
 */
@Getter
@Builder
public class EnrollmentResponse {

    private Long id;

    private Long classId;

    private Long studentId;

    private String status;

}
