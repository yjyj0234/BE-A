package com.project.bea.lecture.repository;

import com.project.bea.lecture.domain.LectureClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureClassRepository extends JpaRepository<LectureClass, Long> {
}
