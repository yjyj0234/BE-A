package com.project.bea.lecture.service;

import com.project.bea.lecture.dto.ClassResponse;
import com.project.bea.lecture.dto.CreateClassRequest;

import java.util.List;

public interface LectureService {
    ClassResponse createClass(CreateClassRequest request);

    List<ClassResponse> getClasses();

    ClassResponse getClass(Long id);

}
