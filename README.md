# Mini-Collaboration-board (Mini Jira)
- 팀 단위 스터디/업무를 위한 티켓 기반 협업 보드  
- Spring Boot 기반 백엔드 API 서버
---
## 🔸목차
- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [ERD 및 핵심 도메인](#ERD-및-핵심-도메인)
- [실행 방법 / 배포 주소](#실행-방법--배포-주소)
- [트러블슈팅 경험](#트러블슈팅-경험)
---
## 1️⃣프로젝트 개요
### 🔹프로젝트 목적
- 단순 CRUD를 넘어 **권한 관리, 확장 가능한 구조, 상태 전환 정책** 등 실무 수준의 백엔드 설계 역량을 검증하기 위한 프로젝트
### 🔹핵심 특징
- JWT 기반 인증/인가
- 팀(보드) 단위 권한 관리
- 티켓 상태 전환 정책
- 이벤트 기반 메일 발송
- AWS 배포 및 운영 환경 구성
---
## 2️⃣기술 스택
### 🔹언어·빌드
- Java 17
- Gradle
- Spring Boot 3.5.9
### 🔹백엔드·API
- Spring Web
- Spring Security + JWT 
- Spring Data JPA + Hibernate
- MySQL
- Bean Validation
- SpringDoc OpenAPI 2.8.6 – Swagger UI
- QueryDSL
### 🔹인프라·운영
- Spring Mail – Gmail SMTP + AWS SES
- Spring Boot Actuator
- AWS RDS
- AWS Secrets Manager
- AWS ECR/ECS
- AWS Route53/ACM
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
### 🔹초대 메일 발송
- 초대 저장 트랜잭션 커밋 후 메일 발송
---
## 4️⃣ERD 및 핵심 도메인
### 🔹핵심 도메인
- **[ User ]**
  - 역할
    - 시스템 내 **사용자 식별 주체**
    - 인증/인가의 기준이 되는 엔티티
  - 추가 설명
    - User는 **다른 도메인(Board, Ticket)의 소유자가 아님**
    - 다른 도메인에서는 **userId만 참조**
- **[ Board ]**
    - 역할
      - **협업 단위(팀/프로젝트)의 중심**
      - 보드 멤버 관리에 대한 최종 책임
    - 추가 설명
        - BoardMember는 **Board 없이는 의미 없음**
        - Board를 통해서만 멤버 추가/제거 가능
- **[ BoardMember ]**
  - User와 Board를 join하는 도메인
- **[ Ticket ]**
    - 역할
        - 업무/할 일의 최소 단위
        - 티켓 상태 전환 규칙 보장
        - 담당자/우선순위/마감일 관리
- **[ Comment ]**
    - 역할
      - 티켓에 대한 의견 기록
      - 작성자/내용 관리
    - 추가 설명
        - BoardMember는 **Board 없이는 의미 없음**
        - Board를 통해서만 멤버 추가/제거 가능
