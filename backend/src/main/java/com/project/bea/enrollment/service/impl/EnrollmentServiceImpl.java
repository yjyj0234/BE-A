package com.project.bea.enrollment.service.impl;

import com.project.bea.enrollment.domain.Enrollment;
import com.project.bea.enrollment.domain.EnrollmentStatus;
import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.enrollment.dto.EnrollmentResponse;
import com.project.bea.enrollment.repository.EnrollmentRepository;
import com.project.bea.enrollment.service.EnrollmentService;
import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.lecture.repository.LectureClassRepository;
import com.project.bea.user.domain.User;
import com.project.bea.user.domain.UserRole;
import com.project.bea.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("학생만 수강 신청할 수 있습니다");
        }

        LectureClass lectureClass = lectureClassRepository.findByIdForUpdate(request.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        System.out.println("요청 classId = " + request.getClassId());
        System.out.println("조회된 강의 id = " + lectureClass.getId());
        System.out.println("capacity = " + lectureClass.getCapacity());
        System.out.println("current = " + lectureClass.getCurrentEnrollmentCount());

        if (!lectureClass.isOpen()) {
            throw new IllegalArgumentException("모집 중인 강의만 신청할 수 있습니다.");
        }

        if (enrollmentRepository.existsByStudentAndLectureClass(student, lectureClass)) {
            throw new IllegalArgumentException("이미 수강 신청한 강의입니다.");
        }

        lectureClass.increaseEnrollmentCount();

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .lectureClass(lectureClass)
                .status(EnrollmentStatus.PENDING)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return EnrollmentResponse.builder()
                .id(savedEnrollment.getId())
                .classId(savedEnrollment.getLectureClass().getId())
                .studentId(savedEnrollment.getStudent().getId())
                .status(savedEnrollment.getStatus().name())
                .build();

    }

    @Override
    @Transactional
    public EnrollmentResponse confirmEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다"));
        enrollment.confirm();

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .classId(enrollment.getLectureClass().getId())
                .studentId(enrollment.getStudent().getId())
                .status(enrollment.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public EnrollmentResponse cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));

        enrollment.cancel();

        enrollment.getLectureClass().decreaseEnrollmentCount();

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .classId(enrollment.getLectureClass().getId())
                .studentId(enrollment.getStudent().getId())
                .status(enrollment.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getMyEnrollments(Long studentId, Pageable pageable) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("학생만 수강 신청 목록을 조회할 수 있습니다.");
        }

        return enrollmentRepository.findByStudentId(studentId, pageable)
                .map(enrollment -> EnrollmentResponse.builder()
                        .id(enrollment.getId())
                        .classId(enrollment.getLectureClass().getId())
                        .studentId(enrollment.getStudent().getId())
                        .status(enrollment.getStatus().name())
                        .build());
    }
}
