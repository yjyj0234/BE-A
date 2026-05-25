package com.project.bea.enrollment.service;

import com.project.bea.enrollment.domain.Enrollment;
import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.enrollment.dto.EnrollmentResponse;
import com.project.bea.enrollment.repository.EnrollmentRepository;
import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.lecture.repository.LectureClassRepository;
import com.project.bea.user.domain.User;
import com.project.bea.user.domain.UserRole;
import com.project.bea.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class EnrollmentServiceTest {
    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LectureClassRepository lectureClassRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Test
    void 수강신청_성공() {

        User creator = userRepository.save(
                User.builder()
                        .name("이크리")
                        .email("creator001@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("김테스")
                        .email("test001@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("Spring 강의")
                        .description("Spring Boot 기초")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        enrollmentService.createEnrollment(request);

        assertThat(lectureClass.getCurrentEnrollmentCount()).isEqualTo(1);
    }

    @Test
    void 같은_강의_중복신청_실패() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator2")
                        .email("creator002@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student2")
                        .email("student002@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("JPA 강의")
                        .description("JPA 기초")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        enrollmentService.createEnrollment(request);

        assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 수강 신청한 강의입니다");
    }

    @Test
    void 정원초과_수강신청_실패() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator3")
                        .email("creator003@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student1 = userRepository.save(
                User.builder()
                        .name("student3")
                        .email("student003@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        User student2 = userRepository.save(
                User.builder()
                        .name("student4")
                        .email("student004@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("정원 1명 강의")
                        .description("정원 초과 테스트")
                        .price(10000)
                        .capacity(1)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request1 =
                new CreateEnrollmentRequest(lectureClass.getId(), student1.getId());

        CreateEnrollmentRequest request2 =
                new CreateEnrollmentRequest(lectureClass.getId(), student2.getId());

        enrollmentService.createEnrollment(request1);

        assertThatThrownBy(() -> enrollmentService.createEnrollment(request2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("모집 중인 강의만 신청할 수 있습니다");
    }

    @Test
    void 결제확정_성공() {

        User creator = userRepository.save(
                User.builder()
                        .name("creator4")
                        .email("creator004@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student5")
                        .email("student005@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("결제 테스트 강의")
                        .description("결제 테스트")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        EnrollmentResponse created =
                enrollmentService.createEnrollment(request);

        EnrollmentResponse confirmed =
                enrollmentService.confirmEnrollment(created.getId(), student.getId());

        assertThat(confirmed.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void 수강신청취소_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator5")
                        .email("creator005@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student6")
                        .email("student006@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("취소 테스트 강의")
                        .description("취소 테스트")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        EnrollmentResponse created =
                enrollmentService.createEnrollment(request);

        EnrollmentResponse cancelled =
                enrollmentService.cancelEnrollment(created.getId(), student.getId());

        assertThat(cancelled.getStatus()).isEqualTo("CANCELLED");
        assertThat(lectureClass.getCurrentEnrollmentCount()).isEqualTo(0);
    }


    @Test
    void 결제후_7일초과_취소_실패() {

        User creator = userRepository.save(
                User.builder()
                        .name("creator6")
                        .email("creator006@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student7")
                        .email("student007@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("7일 제한 테스트")
                        .description("취소 제한")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        EnrollmentResponse created =
                enrollmentService.createEnrollment(request);

        enrollmentService.confirmEnrollment(created.getId(), student.getId());

        Enrollment enrollment = enrollmentRepository.findById(created.getId())
                .orElseThrow();

        ReflectionTestUtils.setField(
                enrollment,
                "paidAt",
                LocalDateTime.now().minusDays(8)
        );

        assertThatThrownBy(() ->
                enrollmentService.cancelEnrollment(created.getId(), student.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 후 7일이 지나 취소할 수 없습니다");
    }

    @Test
    void 이미_결제_확정된_신청_결제확정_실패() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator7")
                        .email("creator007@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student8")
                        .email("student008@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("중복 결제확정 테스트")
                        .description("이미 확정된 신청 테스트")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        EnrollmentResponse created =
                enrollmentService.createEnrollment(request);

        enrollmentService.confirmEnrollment(created.getId(), student.getId());

        assertThatThrownBy(() ->
                enrollmentService.confirmEnrollment(created.getId(), student.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 대기 상태에서만 결제 확정할 수 있습니다");
    }

    @Test
    void 이미취소된_신청_취소_실패() {

        User creator = userRepository.save(
                User.builder()
                        .name("creator8")
                        .email("creator008@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student9")
                        .email("student009@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("중복 취소 테스트")
                        .description("이미 취소된 신청 테스트")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        EnrollmentResponse created =
                enrollmentService.createEnrollment(request);

        enrollmentService.cancelEnrollment(created.getId(), student.getId());

        assertThatThrownBy(() ->
                enrollmentService.cancelEnrollment(created.getId(), student.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 취소된 신청입니다");
    }

    @Test
    void 오픈되지않은_강의_수강신청_실패() {

        User creator = userRepository.save(
                User.builder()
                        .name("creator9")
                        .email("creator009@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student10")
                        .email("student010@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("오픈 전 강의")
                        .description("DRAFT 상태 강의")
                        .price(10000)
                        .capacity(30)
                        .status(ClassStatus.DRAFT)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        CreateEnrollmentRequest request =
                new CreateEnrollmentRequest(lectureClass.getId(), student.getId());

        assertThatThrownBy(() ->
                enrollmentService.createEnrollment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("모집 중인 강의만 신청할 수 있습니다");
    }

    @Test
    void 내_수강신청_목록조회_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator_paging")
                        .email("creator_paging@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student = userRepository.save(
                User.builder()
                        .name("student_paging")
                        .email("student_paging@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        for (int i = 1; i <= 20; i++) {
            LectureClass lectureClass = lectureClassRepository.save(
                    LectureClass.builder()
                            .creator(creator)
                            .title("페이징 테스트 강의 " + i)
                            .description("내 수강신청 목록 페이징 테스트")
                            .price(10000)
                            .capacity(30)
                            .status(ClassStatus.OPEN)
                            .startDate(LocalDateTime.now().plusDays(i))
                            .endDate(LocalDateTime.now().plusDays(i + 30))
                            .build()
            );

            enrollmentService.createEnrollment(
                    new CreateEnrollmentRequest(
                            lectureClass.getId(),
                            student.getId()
                    )
            );
        }

        Page<EnrollmentResponse> firstPage =
                enrollmentService.getMyEnrollments(
                        student.getId(),
                        PageRequest.of(0, 10)
                );

        for (int page = 0; page < firstPage.getTotalPages(); page++) {

            Page<EnrollmentResponse> currentPage =
                    enrollmentService.getMyEnrollments(
                            student.getId(),
                            PageRequest.of(page, 10)
                    );

            System.out.println("===== " + (page + 1) + "페이지 =====");

            currentPage.getContent().forEach(enrollment -> {
                System.out.println(
                        "강의 ID = " + enrollment.getClassId()
                );
            });
        }

        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(firstPage.getTotalElements()).isEqualTo(20);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getNumber()).isEqualTo(0);
        assertThat(firstPage.getSize()).isEqualTo(10);
    }

    @Test
    void 강의별_수강생목록_조회_페이징_성공() {
        User creator = userRepository.save(
                User.builder()
                        .name("creator_class_users_paging")
                        .email("creator_class_users_paging@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        for (int classIndex = 1; classIndex <= 3; classIndex++) {
            LectureClass lectureClass = lectureClassRepository.save(
                    LectureClass.builder()
                            .creator(creator)
                            .title("수강생 목록 페이징 테스트 강의 " + classIndex)
                            .description("강의별 수강생 목록 페이징 테스트")
                            .price(10000)
                            .capacity(30)
                            .status(ClassStatus.OPEN)
                            .startDate(LocalDateTime.now().plusDays(classIndex))
                            .endDate(LocalDateTime.now().plusDays(classIndex + 30))
                            .build()
            );

            int studentCount = 13 + classIndex;
            // classIndex 1 → 14명
            // classIndex 2 → 15명
            // classIndex 3 → 16명

            for (int i = 1; i <= studentCount; i++) {
                User student = userRepository.save(
                        User.builder()
                                .name("class_" + classIndex + "_student_" + i)
                                .email("class_" + classIndex + "_student_" + i + "@test.com")
                                .role(UserRole.STUDENT)
                                .build()
                );

                enrollmentService.createEnrollment(
                        new CreateEnrollmentRequest(
                                lectureClass.getId(),
                                student.getId()
                        )
                );
            }

            Page<EnrollmentResponse> firstPage =
                    enrollmentService.getClassUsers(
                            lectureClass.getId(),
                            creator.getId(),
                            PageRequest.of(0, 10)
                    );

            System.out.println("===== 강의 " + classIndex + " =====");
            System.out.println("전체 수강생 수 = " + firstPage.getTotalElements());
            System.out.println("전체 페이지 수 = " + firstPage.getTotalPages());

            for (int page = 0; page < firstPage.getTotalPages(); page++) {
                Page<EnrollmentResponse> currentPage =
                        enrollmentService.getClassUsers(
                                lectureClass.getId(),
                                creator.getId(),
                                PageRequest.of(page, 10)
                        );

                System.out.println("----- " + (page + 1) + "페이지 -----");

                currentPage.getContent().forEach(enrollment -> {
                    System.out.println("신청 ID = " + enrollment.getId());
                    System.out.println("강의 ID = " + enrollment.getClassId());
                    System.out.println("학생 ID = " + enrollment.getStudentId());
                    System.out.println("상태 = " + enrollment.getStatus());
                    System.out.println("--------------------");
                });
            }

            assertThat(firstPage.getTotalElements()).isEqualTo(studentCount);
            assertThat(firstPage.getTotalPages()).isEqualTo(2);
            assertThat(firstPage.getContent()).hasSize(10);
        }
    }
}
