package com.project.bea.lecture.service;

import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.lecture.dto.ClassResponse;
import com.project.bea.lecture.dto.CreateClassRequest;
import com.project.bea.lecture.repository.LectureClassRepository;
import com.project.bea.user.domain.User;
import com.project.bea.user.domain.UserRole;
import com.project.bea.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class LectureServiceTest {

    @Autowired
    LectureService lectureService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LectureClassRepository lectureClassRepository;

    @Test
    void 크리에이터_강의등록_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator")
                        .email("creator_lecture@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        CreateClassRequest request = new CreateClassRequest(
                creator.getId(),
                "test강의",
                "test 기초",
                10000,
                30,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(30)
        );

        ClassResponse response = lectureService.createClass(request);

        assertThat(response.getTitle()).isEqualTo("test강의");
        assertThat(response.getStatus()).isEqualTo("DRAFT");
        assertThat(response.getCapacity()).isEqualTo(30);
    }

    @Test
    void 일반사용자_강의등록_실패() {
        User student = userRepository.save(
                User.builder()
                        .name("student")
                        .email("student_lecture@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        CreateClassRequest request = new CreateClassRequest(
                student.getId(),
                "잘못된 강의",
                "일반 사용자는 생성 불가",
                10000,
                30,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(30)
        );

        assertThatThrownBy(() -> lectureService.createClass(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("크리에이터만 강의를 생성할 수 있습니다");
    }

    @Test
    void 강의상태_DRAFT에서_OPEN_변경_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator_open")
                        .email("creator_open@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("오픈 테스트 강의")
                        .description("상태 변경 테스트")
                        .price(10000)
                        .capacity(30)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        ClassResponse response =
                lectureService.openClass(lectureClass.getId(), creator.getId());

        assertThat(response.getStatus()).isEqualTo("OPEN");
    }

    @Test
    void 강의상태_OPEN에서_CLOSED_변경_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator_close")
                        .email("creator_close@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("마감 테스트 강의")
                        .description("상태 변경 테스트")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        ClassResponse response =
                lectureService.closeClass(lectureClass.getId(), creator.getId());

        assertThat(response.getStatus()).isEqualTo("CLOSED");
    }

    @Test
    void 다른_크리에이터가_강의상태변경_실패() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator_owner")
                        .email("creator_owner@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User otherCreator = userRepository.save(
                User.builder()
                        .name("creator_other")
                        .email("creator_other@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("권한 테스트 강의")
                        .description("다른 크리에이터 상태 변경 불가")
                        .price(10000)
                        .capacity(30)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        assertThatThrownBy(() ->
                lectureService.openClass(lectureClass.getId(), otherCreator.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("크리에이터만 강의 상태를 변경할 수 있습니다");
    }

    @Test
    void 강의목록_상태필터_OPEN_조회_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator_filter")
                        .email("creator_filter@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("OPEN 강의")
                        .description("OPEN 상태")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("DRAFT 강의")
                        .description("DRAFT 상태")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.DRAFT)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        List<ClassResponse> result = lectureService.getClasses(ClassStatus.OPEN);

        assertThat(result).allMatch(classResponse ->
                classResponse.getStatus().equals("OPEN")
        );
    }

    @Test
    void 강의상세조회_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator_detail")
                        .email("creator_detail@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("상세 조회 강의")
                        .description("현재 신청 인원 포함")
                        .price(10000)
                        .capacity(30)
                        .currentEnrollmentCount(3)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        ClassResponse response = lectureService.getClass(lectureClass.getId());

        assertThat(response.getCurrentEnrollmentCount()).isEqualTo(3);
    }
}