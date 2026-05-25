package com.project.bea.lecture.domain;

import com.project.bea.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "classes")
@AllArgsConstructor
@Builder
public class LectureClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 강의를 만든 크리에이터
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentEnrollmentCount = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClassStatus status = ClassStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void increaseEnrollmentCount() {
        if(this.currentEnrollmentCount >= this.capacity) {
            throw  new IllegalStateException("수강 정원이 초과되었습니다.");
        }
        this.currentEnrollmentCount++;

        closeIfFull();
    }

    public void decreaseEnrollmentCount() {
        if (this.currentEnrollmentCount <= 0) {
            throw new IllegalStateException("신청 인원은 0명보다 작을 수 없습니다.");
        }

        this.currentEnrollmentCount--;
    }

    //신청 가능한 강의 여부
    public  boolean isOpen() {
        return this.status == ClassStatus.OPEN;
    }

    public void closeIfFull() {
        if (this.currentEnrollmentCount >= this.capacity) {
            this.status = ClassStatus.CLOSED;
        }
    }

    public void open() {
        if (this.status != ClassStatus.DRAFT) {
            throw new IllegalStateException("초안 상태의 강의만 모집 시작할 수 있습니다.");
        }

        this.status = ClassStatus.OPEN;
    }

    public void close() {
        if (this.status == ClassStatus.CLOSED) {
            throw new IllegalStateException("이미 마감된 강의입니다.");
        }

        this.status = ClassStatus.CLOSED;
    }
}

