package com.project.bea.enrollment.controller;

import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.enrollment.dto.EnrollmentResponse;
import com.project.bea.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @PatchMapping("/{enrollmentId}/confirm")
    public EnrollmentResponse confirmEnrollment(@PathVariable Long enrollmentId,@RequestParam Long studentId) {
        return enrollmentService.confirmEnrollment(enrollmentId, studentId);
    }

    @PatchMapping("/{enrollmentId}/cancel")
    public EnrollmentResponse cancelEnrollment(@PathVariable Long enrollmentId,@RequestParam Long studentId) {
        return enrollmentService.cancelEnrollment(enrollmentId, studentId);
    }

    @GetMapping("/getMyEnrollments")
    public Page<EnrollmentResponse> getMyEnrollments(@RequestParam Long studentId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return enrollmentService.getMyEnrollments(studentId, pageable);
    }

    @GetMapping("getClassUsers/{classId}/users")
    public Page<EnrollmentResponse> getClassUsers(
            @PathVariable Long classId, @RequestParam Long creatorId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return enrollmentService.getClassUsers(classId, creatorId, pageable);
    }
}
