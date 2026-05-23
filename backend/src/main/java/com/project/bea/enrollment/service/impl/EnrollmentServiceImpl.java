package com.project.bea.enrollment.service.impl;

import com.project.bea.enrollment.domain.Enrollment;
import com.project.bea.enrollment.domain.EnrollmentStatus;
import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.enrollment.dto.EnrollmentResponse;
import com.project.bea.enrollment.repository.EnrollmentRepository;
import com.project.bea.enrollment.service.EnrollmentService;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.lecture.repository.LectureClassRepository;
import com.project.bea.user.domain.User;
import com.project.bea.user.domain.UserRole;
import com.project.bea.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * file: EnrollmentServiceImpl.java
 * author: 손현정
 * description: 수강 신청 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final LectureClassRepository lectureClassRepository;

    private final UserRepository userRepository;

    @Override
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("학생만 수강 신청할 수 있습니다");
        }

        LectureClass lectureClass = lectureClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (enrollmentRepository.existsByStudentAndLectureClass(student, lectureClass)) {
            throw new IllegalArgumentException("이미 수강 신청한 강의입니다.");
        }

        if(lectureClass.getCurrentEnrollmentCount() >= lectureClass.getCapacity()) {
            throw new IllegalArgumentException("수강 정원이 초과되었습니다.");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .lectureClass(lectureClass)
                .status(EnrollmentStatus.PENDING)
                .build();

        lectureClass.increaseEnrollmentCount();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return EnrollmentResponse.builder()
                .id(savedEnrollment.getId())
                .classId(savedEnrollment.getLectureClass().getId())
                .studentId(savedEnrollment.getStudent().getId())
                .status(savedEnrollment.getStatus().name())
                .build();

    }
}
