package com.project.bea.enrollment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnrollmentResponse {

    private Long id;

    private Long classId;

    private Long studentId;

    private String status;

}
