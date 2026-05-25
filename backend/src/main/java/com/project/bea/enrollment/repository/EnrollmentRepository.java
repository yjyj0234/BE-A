package com.project.bea.enrollment.repository;

import com.project.bea.enrollment.domain.Enrollment;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndLectureClass(User student, LectureClass lectureClass);

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByLectureClassId(Long classId, Pageable pageable);

}
