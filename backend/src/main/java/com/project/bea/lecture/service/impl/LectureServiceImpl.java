package com.project.bea.lecture.service.impl;

import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.lecture.dto.ClassResponse;
import com.project.bea.lecture.dto.CreateClassRequest;
import com.project.bea.lecture.repository.LectureClassRepository;
import com.project.bea.lecture.service.LectureService;
import com.project.bea.user.domain.User;
import com.project.bea.user.domain.UserRole;
import com.project.bea.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {
    private final LectureClassRepository lectureClassRepository;
    private final UserRepository userRepository;

    //강의 개설
    @Override
    public ClassResponse createClass(CreateClassRequest request) {
        User creator = userRepository.findById(request.getCreatorId())
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (creator.getRole() != UserRole.CREATOR) {
            throw new IllegalArgumentException("크리에이터만 강의를 생성할 수 있습니다.");
        }

        LectureClass lectureClass = LectureClass.builder()
                .creator(creator)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .capacity(request.getCapacity())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        LectureClass savedClass = lectureClassRepository.save(lectureClass);

        return ClassResponse.builder()
                .id(savedClass.getId())
                .title(savedClass.getTitle())
                .description(savedClass.getDescription())
                .price(savedClass.getPrice())
                .capacity(savedClass.getCapacity())
                .status(savedClass.getStatus().name())
                .startDate(savedClass.getStartDate())
                .endDate(savedClass.getEndDate())
                .build();
    }

    @Override
    public List<ClassResponse> getClasses(ClassStatus status) {
        List<LectureClass> classes;

        if (status == null) {
            classes = lectureClassRepository.findAll();
        } else {
            classes = lectureClassRepository.findByStatus(status);
        }

        return classes.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ClassResponse getClass(Long id) {

        LectureClass lectureClass = lectureClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        return toResponse(lectureClass);
    }

    @Transactional
    @Override
    public ClassResponse openClass(Long classId, Long creatorId) {

        LectureClass lectureClass = lectureClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (!lectureClass.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("크리에이터만 강의 상태를 변경할 수 있습니다.");
        }

        lectureClass.open();

        return toResponse(lectureClass);
    }

    @Transactional
    @Override
    public ClassResponse closeClass(Long classId, Long creatorId) {
        LectureClass lectureClass = lectureClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        if (!lectureClass.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("크리에이터만 강의 상태를 변경할 수 있습니다.");
        }

        lectureClass.close();

        return toResponse(lectureClass);
    }

    //강의 return 용
    private ClassResponse toResponse(LectureClass lectureClass) {
        return ClassResponse.builder()
                .id(lectureClass.getId())
                .title(lectureClass.getTitle())
                .description(lectureClass.getDescription())
                .price(lectureClass.getPrice())
                .capacity(lectureClass.getCapacity())
                .currentEnrollmentCount(lectureClass.getCurrentEnrollmentCount())
                .status(lectureClass.getStatus().name())
                .startDate(lectureClass.getStartDate())
                .endDate(lectureClass.getEndDate())
                .build();
    }
}
