package com.project.bea.enrollment.service;

import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.enrollment.dto.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * file: EnrollmentService.java
 * author: 손현정
 * description: 수강 신청 관련 비즈니스 로직의 인터페이스
 */
public interface EnrollmentService {

    EnrollmentResponse createEnrollment(CreateEnrollmentRequest request);

    EnrollmentResponse confirmEnrollment(Long enrollmentId, Long studentId);

    EnrollmentResponse cancelEnrollment(Long enrollmentId, Long studentId);

    Page<EnrollmentResponse> getMyEnrollments(Long studentId, Pageable pageable);

    Page<EnrollmentResponse> getClassUsers(Long classId, Long creatorId, Pageable pageable);
}
