# Mini-Collaboration-board (Mini Jira) [![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://beetledev.kr/swagger-ui/index.html)
>팀 단위 스터디/업무를 위한 티켓 기반 협업 보드  
>Spring Boot 기반 백엔드 API 서버
---
## 📌목차
- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [ERD 및 핵심 도메인](#ERD-및-핵심-도메인)
- [배포 & 운영](#배포-&-운영)
- [향후 개선 사항](#향후-개선-사항)
---
## 1️⃣프로젝트 개요
### 🔹프로젝트 목적
- 단순 CRUD를 넘어 **권한 관리, 확장 가능한 도메인 설계, 상태 전환 정책** 등 실무 수준의 백엔드 설계 역량을 검증하기 위해 제작한 개인 프로젝트입니다.
### 🔹핵심 특징
| 특징 | 설명 |
|---|---|
| 인증 / 인가 | JWT 기반 Stateless 인증 및 보드 단위 역할 관리 |
| 권한 관리 | OWNER / ADMIN / MEMBER 3단계 역할 기반 접근 제어 |
| 이벤트 기반 메일 | 트랜잭션 커밋 이후 비동기 초대 메일 발송 |
| 클라우드 배포 | AWS ECS(Fargate) + RDS + Secrets Manager를 활용한 운영 환경 구성 |
---
## 2️⃣기술 스택
### 🔹언어·빌드
- Java 17
- Gradle
- Spring Boot 3.5.9
### 🔹백엔드·API
| 분류 | 기술 |
|---|---|
| 웹 프레임워크 | Spring Web, Spring Security |
| 인증 | JWT |
| ORM | Spring Data JPA, Hibernate, QueryDSL |
| 데이터베이스 | MySQL |
| 유효성 검사 | Bean Validation |
| API 문서 | SpringDoc OpenAPI 2.8.6 (Swagger UI) |
### 🔹인프라·운영
| 분류 | 기술 |
|---|---|
| 메일 발송 | Spring Mail (Gmail SMTP), AWS SES |
| 모니터링 | Spring Boot Actuator |
| 데이터베이스 | AWS RDS (Aurora MySQL) |
| 비밀 관리 | AWS Secrets Manager |
| 컨테이너 | AWS ECR, ECS (Fargate) |
| 도메인 · 인증서 | AWS Route 53, ACM |
| CI/CD | GitHub Actions, Docker |
---
## 3️⃣주요 기능
### 🔹인증 / 인가
- JWT 기반 로그인
- 보드 단위 권한(OWNER / ADMIN / MEMBER)
- 리소스 접근 시 권한 검증
### 🔹티켓 관리
- 티켓 CRUD
- 상태 전환 정책
    - TODO → IN_PROGRESS → DONE
- 담당자 / 우선순위 / 마감일 관리
### 🔹초대 메일 발송
- 트랜잭션 커밋 후 메일 발송 — TransactionalEventListener를 활용해 초대 저장과 메일 발송의 책임을 분리
- 메일 발송 실패가 초대 트랜잭션에 영향을 주지 않도록 격리
---
## 4️⃣ERD 및 핵심 도메인
### 🔹핵심 도메인
```
User ──< BoardMember >── Board
                            │
                         Ticket
                            │
                         Comment
```

| 도메인 | 역할 |
|---|---|
| **User** | 시스템 내 사용자 식별 주체. 인증/인가의 기준 엔티티 |
| **Board** | 협업 단위(팀/프로젝트)의 중심. 멤버 관리 최종 책임 |
| **BoardMember** | User ↔ Board 간 다대다 관계를 해소하는 중간 도메인. 역할 정보 포함 |
| **Ticket** | 업무/할 일의 최소 단위. 상태 전환 규칙, 담당자, 우선순위, 마감일 관리 |
| **Comment** | 티켓에 대한 의견 기록. 작성자 및 내용 관리 |
### 🔹권한(OWNER / ADMIN / MEMBER) 모델링
| 항목 | OWNER | ADMIN | MEMBER |
|------|:-----:|:-----:|:------:|
| Board 수정 | ✅ | ✅ | ❌ |
| Board 삭제 | ✅ | ❌ | ❌ |
| Ticket CRU | ✅ | ✅ | ✅ |
| Ticket D | ✅ | ✅ | ❌ |
| BoardMember(MEMBER) 초대 | ✅ | ✅ | ❌ |
| BoardMember(ADMIN) 초대 | ✅ | ❌ | ❌ |
| ADMIN 권한 부여 | ✅ | ❌ | ❌ |
### 🔹ERD
![mini-collaboration-board.png](docs%2Ferd%2Fmini-collaboration-board.png)
---
## 5️⃣배포 & 운영
### 🔹AWS 아키텍쳐
```
Route 53
   │
  ACM (HTTPS)
   │
  ECS Fargate (App Container)
   │          │
  ECR       Secrets Manager
   │
  RDS Aurora (MySQL)
   │
  SES (메일 발송)
```
### 🔹 배포 
```
GitHub Push
    │
GitHub Actions (CI)
    ├── 빌드 및 테스트
    └── Docker 이미지 빌드 → ECR Push
                                │
                          ECS 서비스 배포 (CD)
```
---
## 6️⃣향후 개선 사항
- **실시간 이벤트 알림** — WebSocket 또는 SSE 기반 티켓 상태 변경 알림
- **네트워크 보안 강화** — ECS / ECR / RDS를 프라이빗 서브넷으로 이전하고 NAT Gateway 구성
- **프론트엔드 구현** — React 또는 Vue.js를 활용한 UI 개발
- **테스트 커버리지 확보** — 단위 테스트 및 통합 테스트 추가
- **티켓 상태 전환** - 티켓 상태를 정해진 플로우에 따라서만 전환 가능하도록 강제
- **Refresh Token 추가**
