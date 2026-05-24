package com.project.bea.enrollment.service;

import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.enrollment.dto.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse createEnrollment(CreateEnrollmentRequest request);

    EnrollmentResponse confirmEnrollment(Long enrollmentId);

    EnrollmentResponse cancelEnrollment(Long enrollmentId);

    Page<EnrollmentResponse> getMyEnrollments(Long studentId, Pageable pageable);
}
