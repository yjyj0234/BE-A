# BE-A 수강 신청 시스템

## 프로젝트 개요

강의 등록 및 수강 신청 기능을 제공하는 백엔드 시스템입니다.

사용자는 강의에 수강 신청 및 결제 확정/취소를 진행할 수 있으며,
크리에이터는 강의를 생성하고 모집 상태를 관리할 수 있습니다.

또한 정원 제한과 동시성 문제를 고려하여 구현하였습니다.

---

## 기술 스택

### Backend
- Java 17
- Spring Boot 3
- Spring Data JPA
- Hibernate

### Database
- PostgreSQL

### Build Tool
- Gradle

### Documentation
- Swagger (SpringDoc OpenAPI)

### Test
- JUnit5
- AssertJ

---

## 실행 방법

### 1. 프로젝트 클론

```bash
git clone https://github.com/yjyj0234/BE-A.git
cd backend
```

---

### 2. PostgreSQL 데이터베이스 생성

```sql
CREATE DATABASE bea;
```

---

### 3. application.yml 설정

`src/main/resources/application.yml`

```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bea
    username: postgres
    password: 비밀번호

  jpa:
    hibernate:
      ddl-auto: update

    properties:
      hibernate:
        format_sql: true

    show-sql: true

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

---

### 4. 프로젝트 실행

Mac / Linux

```bash
./gradlew bootRun
```

Windows

```bash
gradlew.bat bootRun
```

---

### 5. Swagger 접속

```text
http://localhost:8080/swagger-ui/index.html
```

---

### 6. 테스트 실행

전체 테스트 실행:

```bash
./gradlew test
```

특정 테스트 실행:

```bash
./gradlew test --tests "com.project.bea.enrollment.service.EnrollmentServiceTest"
```

---

## 요구사항 해석 및 가정

- 인증/인가(JWT)는 과제 범위에서 제외된 것으로 판단하여 구현하지 않았습니다.
- 로그인 사용자는 요청의 `creatorId`, `studentId`를 통해 구분하도록 구현하였습니다.
- 강의 상태는 `DRAFT → OPEN → CLOSED` 흐름으로 관리하였습니다.
- `OPEN` 상태의 강의만 수강 신청 가능하도록 제한하였습니다.
- 정원이 모두 찬 경우 자동으로 `CLOSED` 상태가 되도록 구현하였습니다.
- 결제 기능은 외부 결제 연동 없이 상태 변경(`PENDING → CONFIRMED`)으로 대체하였습니다.
- 수강 취소는 결제 후 7일 이내만 가능하도록 구현하였습니다.

---

## 설계 결정과 이유

### 1. 상태(Enum) 기반 설계

강의(Class)와 수강 신청(Enrollment)의 상태를 Enum으로 관리하여
상태 전이를 명확하게 표현하였습니다.

#### Lecture 상태
- DRAFT
- OPEN
- CLOSED

#### Enrollment 상태
- PENDING
- CONFIRMED
- CANCELLED

---

### 2. 비즈니스 로직을 Entity 내부에 배치

상태 변경 및 검증 로직을 Entity 내부 메서드로 관리하였습니다.

예시:
- `open()`
- `close()`
- `confirm()`
- `cancel()`

이를 통해 상태 변경 책임을 Entity 내부로 응집시켰습니다.

---

### 3. 동시성 처리

마지막 남은 자리를 여러 사용자가 동시에 신청하는 상황을 고려하여
비관적 락(Pessimistic Lock)을 사용하였습니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

이를 통해 정원 초과 신청을 방지하였습니다.

---

### 4. 페이지네이션 적용

대량 데이터 조회를 고려하여 다음 기능에 페이지네이션을 적용하였습니다.

- 내 수강 신청 목록 조회
- 강의별 수강생 목록 조회

---

## 미구현 / 제약사항

### 미구현 항목
- 대기열(waitlist) 기능
- 기간 만료 자동 CLOSED 처리
- JWT 기반 인증/인가

### 제약사항
- 실제 로그인 기능 없이 요청 파라미터 기반으로 사용자 권한을 구분하였습니다.
- 결제 시스템은 실제 외부 연동 없이 상태 변경으로 대체하였습니다.

---

## AI 활용 범위

과제 진행 과정에서 다음 범위 내에서 AI를 활용하였습니다.

- 테스트 코드 작성 방향 검토
- 동시성 처리 방식(Pessimistic Lock) 학습 및 검토
- README 구조 정리
- 예외 처리 및 API 설계 방향 검토

단순 코드 복사보다는 구현 과정에서 발생한 문제를 분석하고,
설계 방향을 검토하는 용도로 활용하였습니다.

---

## API 목록 및 예시

### Class API

#### 1. 강의 등록

```http
POST /classes
```

Request

```json
{
  "creatorId": 1,
  "title": "Spring Boot 강의",
  "description": "백엔드 기초 강의",
  "price": 10000,
  "capacity": 30,
  "startDate": "2026-06-01T10:00:00",
  "endDate": "2026-06-30T18:00:00"
}
```

---

#### 2. 강의 목록 조회

```http
GET /classes/getClasses
```

---

#### 3. 상태별 강의 조회

```http
GET /classes/getClasses?status=OPEN
```

---

#### 4. 강의 상세 조회

```http
GET /classes/{id}
```

---

#### 5. 강의 모집 시작

```http
PATCH /classes/{classId}/open?creatorId=1
```

---

#### 6. 강의 모집 마감

```http
PATCH /classes/{classId}/close?creatorId=1
```

---

### Enrollment API

#### 1. 수강 신청

```http
POST /enrollments
```

Request

```json
{
  "classId": 1,
  "studentId": 2
}
```

---

#### 2. 결제 확정

```http
PATCH /enrollments/{id}/confirm?studentId=2
```

---

#### 3. 수강 취소

```http
PATCH /enrollments/{id}/cancel?studentId=2
```

---

#### 4. 내 수강 신청 목록 조회

```http
GET /enrollments/me?studentId=2&page=0&size=10
```

---

#### 5. 강의별 수강생 목록 조회

```http
GET /enrollments/classes/{classId}/students?creatorId=1&page=0&size=10
```

---

## 데이터 모델 설명

### User

| 필드 | 설명 |
|---|---|
| id | 사용자 ID |
| name | 사용자 이름 |
| email | 이메일 |
| role | 사용자 역할(CREATOR/STUDENT) |

---

### LectureClass

| 필드 | 설명 |
|---|---|
| id | 강의 ID |
| creator_id | 강의 생성자 |
| title | 강의 제목 |
| description | 강의 설명 |
| price | 가격 |
| capacity | 최대 정원 |
| current_enrollment_count | 현재 신청 인원 |
| status | 강의 상태 |

---

### Enrollment

| 필드 | 설명 |
|---|---|
| id | 신청 ID |
| class_id | 강의 ID |
| student_id | 학생 ID |
| status | 신청 상태 |
| paid_at | 결제 시간 |
| cancelled_at | 취소 시간 |

---

## 테스트 실행 방법

### 전체 테스트 실행

```bash
./gradlew test
```

---

### 특정 테스트 실행

```bash
./gradlew test --tests "com.project.bea.enrollment.service.EnrollmentServiceTest"
```

---

### 테스트 내용

- 강의 생성 성공/실패
- 강의 상태 변경
- 상태별 강의 조회
- 수강 신청 성공/실패
- 중복 신청 방지
- 정원 초과 방지
- 동시성 테스트
- 결제 확정
- 수강 취소
- 7일 초과 취소 실패
- 페이지네이션 조회
