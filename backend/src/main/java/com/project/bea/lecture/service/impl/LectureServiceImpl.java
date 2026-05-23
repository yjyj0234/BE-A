package com.project.bea.lecture.service.impl;

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
    public List<ClassResponse> getClasses() {
        return lectureClassRepository.findAll().stream()
                .map(lectureClass -> ClassResponse.builder()
                        .id(lectureClass.getId())
                        .title(lectureClass.getTitle())
                        .description(lectureClass.getDescription())
                        .price(lectureClass.getPrice())
                        .capacity(lectureClass.getCapacity())
                        .startDate(lectureClass.getStartDate())
                        .endDate(lectureClass.getEndDate())
                        .build())
                .toList();
    }

    @Override
    public ClassResponse getClass(Long id) {

        LectureClass lectureClass = lectureClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        return ClassResponse.builder()
                .id(lectureClass.getId())
                .title(lectureClass.getTitle())
                .description(lectureClass.getDescription())
                .price(lectureClass.getPrice())
                .capacity(lectureClass.getCapacity())
                .startDate(lectureClass.getStartDate())
                .endDate(lectureClass.getEndDate())
                .build();
    }
}
