package com.project.bea.lecture.repository;

import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.domain.LectureClass;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * file: LectureClassRepository.java
 * author: 손현정
 * description: 강의 데이터 접근 및 비관적 락 조회를 담당하는 JPA Repository
 */
public interface LectureClassRepository extends JpaRepository<LectureClass, Long> {

    //동시성 처리 관련 비관적 락 메서드
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from LectureClass a where a.id = :classId")
    Optional<LectureClass> findByIdForUpdate(@Param("classId") Long classId);

    List<LectureClass> findByStatus(ClassStatus status);
}
