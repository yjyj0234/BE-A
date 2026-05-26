package com.project.bea.enrollment.domain;

import com.project.bea.lecture.domain.LectureClass;
import com.project.bea.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * file: Enrollment.java
 * author: 손현정
 * description: 수강 신청 정보와 신청 상태를 관리하는 엔티티
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "enrollments")
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 강의 신청인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private LectureClass lectureClass;

    // 어떤 학생이 신청했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void confirm() {
        if (this.status != EnrollmentStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태에서만 결제 확정할 수 있습니다.");
        }

        this.status = EnrollmentStatus.CONFIRMED;
        this.paidAt = LocalDateTime.now();

    }

    public void cancel() {
        if (this.status == EnrollmentStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 신청입니다.");
        }

        if (this.status == EnrollmentStatus.CONFIRMED) {
            if (this.paidAt == null) {
                throw new IllegalStateException("결제 확정 시간이 존재하지 않습니다.");
            }

            if (this.paidAt.plusDays(7).isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("결제 후 7일이 지나 취소할 수 없습니다.");
            }
        }

        this.status = EnrollmentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
}