package com.project.bea.enrollment.service;

import com.project.bea.enrollment.dto.CreateEnrollmentRequest;
import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.lecture.repository.LectureClassRepository;
import com.project.bea.user.domain.User;
import com.project.bea.user.domain.UserRole;
import com.project.bea.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EnrollmentConcurrencyTest {

    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LectureClassRepository lectureClassRepository;


    String unique = String.valueOf(System.currentTimeMillis());

    @Test
    void 동시에_여러명이_마지막자리_신청시_한명만_성공() throws InterruptedException {
        User creator = userRepository.save(
                User.builder()
                        .name("김크리에")
                        .email("creator"+unique +"@test.com")
                        .role(UserRole.CREATOR)
                        .build()
        );

        User student1 = userRepository.save(
                User.builder()
                        .name("손학생")
                        .email("test"+unique+"@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        User student2 = userRepository.save(
                User.builder()
                        .name("김테스")
                        .email("test2"+unique+"@test.com")
                        .role(UserRole.STUDENT)
                        .build()
        );

        LectureClass lectureClass = lectureClassRepository.save(
                LectureClass.builder()
                        .creator(creator)
                        .title("동시성 테스트 강의")
                        .description("마지막 자리 신청 테스트")
                        .price(10000)
                        .capacity(1)
                        .status(ClassStatus.OPEN)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        Long classId = lectureClass.getId();

        Runnable task1 = () -> {
            try {
                startLatch.await();
                enrollmentService.createEnrollment(
                        new CreateEnrollmentRequest(classId, student1.getId())
                );
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println("student1 실패 사유 = " + e.getMessage());
                failCount.incrementAndGet();
            } finally {
                endLatch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                startLatch.await();
                enrollmentService.createEnrollment(
                        new CreateEnrollmentRequest(classId, student2.getId())
                );
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println("student2 실패 사유 = " + e.getMessage());
                failCount.incrementAndGet();
            } finally {
                endLatch.countDown();
            }
        };

        executorService.submit(task1);
        executorService.submit(task2);

        startLatch.countDown();
        endLatch.await();

        System.out.println("성공 수 = " + successCount.get());
        System.out.println("실패 수 = " + failCount.get());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        executorService.shutdown();
    }
}