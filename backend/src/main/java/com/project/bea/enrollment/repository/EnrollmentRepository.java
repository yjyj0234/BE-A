package com.project.bea.enrollment.repository;

import com.project.bea.enrollment.domain.Enrollment;
import com.project.bea.enrollment.domain.EnrollmentStatus;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * file: EnrollmentRepository.java
 * author: 손현정
 * description: 수강 신청 데이터 접근을 담당하는 JPA Repository
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndLectureClass(User student, LectureClass lectureClass);

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByLectureClassIdAndStatusNot(Long classId, EnrollmentStatus status, Pageable pageable);

}
