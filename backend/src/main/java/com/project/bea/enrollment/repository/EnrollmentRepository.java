package com.project.bea.enrollment.repository;

import com.project.bea.enrollment.domain.Enrollment;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndLectureClass(User student, LectureClass lectureClass);
}
