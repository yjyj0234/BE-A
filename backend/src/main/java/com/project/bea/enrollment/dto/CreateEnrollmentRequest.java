package com.project.bea.enrollment.dto;

import lombok.Getter;

@Getter
public class CreateEnrollmentRequest {

    private Long classId;

    private Long studentId;
}