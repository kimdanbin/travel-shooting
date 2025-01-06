# 🗺 Travel Shooting
- Travel Shooting은 개발자들이 Trouble Shooting을 작성하며 문제 해결 과정을 기록하듯, 사용자들도 자신의 여행 경험을 기록하자는 의미를 담았습니다.
- 협력 업체에서 제공 해주는 레저/티켓을 사용한 사용자들은 포스터를 작성할 수 있습니다.
- 포스터를 작성하여 다른 사용자들에게 실질적인 여행 정보를 제공하고, 유용한 여행지를 추천할 수 있습니다.

---------

# 🌟 핵심 기능
- 레저/티켓
  - 협력 업체는 관리자를 통해 업체 등록을 할 수 있습니다.
    - 관리자가 직접 업체에 대한 검증을 하고, 업체를 등록 합니다.
    - 등록된 업체는 파트너 권한을 얻습니다.
  - 파트너는 레저/티켓 상품 등록을 할 수 있습니다.
  - 사용자는 레저/티켓 상품에 대해 예약 및 결제를 할 수 있습니다.
    - 카카오페이를 사용해 결제할 수 있습니다.
- 여행 코스 검색
  - 레저/티켓 서비스를 이용한 사용자는 포스터를 통해 여행 경험을 기록할 수 있습니다.
  - 포스터를 좋아요 하거나 댓글을 남길 수 있습니다.
  - 포스터를 기반으로 다양한 여행 코스를 검색할 수 있습니다.
- 장소 검색
  - 여행 계획 시간을 줄일 수 있도록 장소 검색 기능도 제공합니다.
    - 장소명
    - 지번 주소
    - 도로명 주소
    - 전화번호
    - 경도
    - 위도
   
---------

# 📅 최종 프로젝트 일정
## 2025.01.02 ~ 2025.01.20
- 주제 및 기술스택 선정
- 요구사항 작성
- 설계
  - 와이어프레임
  - ERD
  - API 명세서
  - 아키텍처 구조
- 1차 기능 구현
- 리팩토링
- 중간 발표회 자료 제작 및 제출

## 2025.01.21 ~ 2025.02.09
- 중간 발표회
- 중간 발표 피드백 개선
- 2차 기능 구현
- 화면 구현
- 최종 발표회 자료 제작 및 제출

## 2025.02.10
- 최종 발표회

---------

# ⚙ 기술 스택
![Spring](https://img.shields.io/badge/Spring-6DB33F.svg?&style=for-the-badge&logo=Spring&logoColor=white)
<img src="https://img.shields.io/badge/spring%20security-5.7.0-green?logo=springsecurity&logoColor=white" alt="Spring Security Badge">
![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?&style=for-the-badge&logo=MySQL&logoColor=white)
<img src="https://img.shields.io/badge/redis-5.0+-red?logo=redis&logoColor=white" alt="Redis Badge">
<img src="https://img.shields.io/badge/docker-latest-blue?logo=docker&logoColor=white" alt="Docker Badge">
<img src="https://img.shields.io/badge/AWS-Active-brightgreen?logo=amazonaws&logoColor=white" alt="AWS Badge">
<img src="https://img.shields.io/badge/Kakao%20Login-Active-brightgreen?logo=kakao&logoColor=white" alt="Kakao Login Badge">
<img src="https://img.shields.io/badge/KakaoPay-Active-brightgreen?logo=kakaopay&logoColor=white" alt="KakaoPay Badge">
<img src="https://img.shields.io/badge/Kakao%20Map-Active-brightgreen?logo=kakao&logoColor=white" alt="Kakao Map Badge">
<img src="https://img.shields.io/badge/AWS%20Route%2053-Active-brightgreen?logo=aws&logoColor=white" alt="AWS Route 53 Badge">

- Spring Boot : 3.4.1
- Java : 17
- IDE : IntelliJ
- DB : MySQL
- Kakao API
- Redis
- Spring Security
- AWS
- Docker

## 🤹‍♀💡 Q&A 
**👨‍🏫 Q1. Spring 프레임워크를 선택한 이유가 무엇인가요? </br>**
👩 Spring은 객체 지향 언어가 가진 특징을 최대로 활용할 수 있는 프레임워크 입니다. 내장 서버(Tomcat 등)를 지원하기 때문에 별도의 웹 서버 설정 없이 실행할 수 있고, 통합 테스트와 단위 테스트를 지원하기 때문에 테스트 가능한 애플리케이션을 구현할 수 있습니다.

**👨‍🏫 Q2. 객체 지향 언어의 특징은 무엇인가요? </br>**
👩 객체 지향 언어의 특징으로 캡슐화, 상속, 추상화, 다형성이 있습니다.
- 외부에서 직접 데이터 및 메서드에 접근하지 못하도록 제한할 수 있습니다. 
- 부모 클래스에 정의된 변수 및 메서드를 자식 클래스에서 상속받아 사용함으로써 코드를 재사용 하거나 재정의 할 수 있습니다.
- 공통 기능을 정의하여 여러 클래스에서 일관되게 사용할 수 있습니다.
- 동일한 이름의 메서드를 파라미터만 다르게 정의할 수 있고, 상속받은 메서드를 자식 클래스에서 재정의 할 수 있습니다.

**👨‍🏫 Q3. MySQL 데이터베이스를 선택한 이유가 무엇인가요? </br>**
👩 MySQL 데이터베이스는 여러 기업에서 많이 사용하고 있는 오픈 소스 관계형 데이터베이스이며 안정적이고 사용하기 쉽습니다. 그리고 모든 팀원이 익숙한 데이터베이스이기 때문에 MySQL 데이터베이스를 사용하였습니다.

**👨‍🏫 Q4. Spring Security를 선택한 이유가 무엇인가요? </br>**
👩 Spring Security는 Spring 프레임워크에서 애플리케이션의 보안을 관리할 때 사용합니다. Spring Security를 적용함으로써 사용자의 인증 및 권한에 따른 인가를 관리할 수 있습니다. 필요에 따라 커스터마이징하여 세부적으로 관리할 수도 있습니다.

**👨‍🏫 Q5. Redis를 선택한 이유가 무엇인가요? </br>**
👩 여러 사람이 동시에 결제할 때 동시성 이슈가 발생할 수 있습니다. Redis는 분산 락을 구현할 수 있는 다양한 라이브러리와 기능을 제공하기 때문에 동시에 자원에 접근하는 현상을 제어할 수 있습니다.

**👨‍🏫 Q6. 어떤 배포 방법을 선택했나요? </br>**
👩 GitHub Action을 이용한 CI/CD 방법을 선택했습니다. 배포 과정에서 Docker와 AWS EC2를 사용합니다.

---------

# 👨‍💻 기능별 담당자
|담당자|역할|1차 기능|2차 기능|블로그 주소|깃허브 주소|
|:----|:----|:----|:----|:----|:----|
|김지연|리더|1. 장소 검색 <br> 2. Kakao Map 기반 음식점 정보 저장 <br> 3. 레저/티켓 예약 CRD <br> 4. 레저/티켓 결제|-|https://velog.io/@yeoni9094/posts </br> https://blog.naver.com/yeondata|https://github.com/jiyeon0926|
|김단빈|부리더|1. 포스터 CUD <br> 2. 첨부파일|1. 배포|https://dreamcompass.tistory.com/|https://github.com/kimdanbin|
|안정민|팀원|1. JWT <br> 2. 인증/인가 <br> 3. 사용자 CRUD <br> 4. 포스터 검색|-|https://velog.io/@devtony/posts|https://github.com/JeongMinAhnn|
|이아름|팀원|1. 레저/티켓 업체 CRUD <br> 2. 레저/티켓 상품 CRUD <br> 3. 신고|1. 카카오 로그인|https://muerha.tistory.com/|https://github.com/aaahreum|
|박시환|팀원|1. 댓글 CRUD <br> 2. 좋아요 <br> 3. 맛집 검색|1. 대댓글 CRUD|||

---------

# 📝 API 명세서 및 ERD
- https://www.notion.so/teamsparta/6-2128906a055f44c6b835197d9dbbbaae
