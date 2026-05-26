package com.project.bea.user.repository;

import com.project.bea.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * file: UserRepository.java
 * author: 손현정
 * description: 사용자 데이터 접근을 담당하는 JPA Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
