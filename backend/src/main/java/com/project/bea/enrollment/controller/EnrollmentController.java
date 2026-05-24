package com.project.bea.enrollment.controller;

import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.enrollment.dto.EnrollmentResponse;
import com.project.bea.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/createEnrollment")
    public EnrollmentResponse createEnrollment(@RequestBody CreateEnrollmentRequest request) {
        return enrollmentService.createEnrollment(request);
    }

    @PatchMapping("/confirm/{enrollmentId}")
    public EnrollmentResponse confirmEnrollment(@PathVariable Long enrollmentId) {
        return enrollmentService.confirmEnrollment(enrollmentId);
    }

    @PatchMapping("/cancel/{enrollmentId}")
    public EnrollmentResponse cancelEnrollment(@PathVariable Long enrollmentId) {
        return enrollmentService.cancelEnrollment(enrollmentId);
    }

    @GetMapping("/getMyEnrollments")
    public Page<EnrollmentResponse> getMyEnrollments(@RequestParam Long studentId, Pageable pageable) {
        return enrollmentService.getMyEnrollments(studentId, pageable);
    }
}
