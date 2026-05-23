package com.project.bea.enrollment.repository;

import com.project.bea.enrollment.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
