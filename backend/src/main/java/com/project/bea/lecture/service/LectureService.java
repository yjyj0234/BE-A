package com.project.bea.lecture.service;

import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.dto.ClassResponse;
import com.project.bea.lecture.dto.CreateClassRequest;

import java.util.List;

/**
 * file: LectureService.java
 * author: 손현정
 * description: 강의 관련 비즈니스 로직의 인터페이스
 */
public interface LectureService {
    ClassResponse createClass(CreateClassRequest request);

    List<ClassResponse> getClasses(ClassStatus status);

    ClassResponse getClass(Long id);

    ClassResponse openClass(Long classId, Long creatorId);

    ClassResponse closeClass(Long classId, Long creatorId);

}
