# 🗺 Travel Shooting
- Travel Shooting은 개발자들이 Trouble Shooting을 작성하며 문제 해결 과정을 기록하듯, 사용자들도 자신의 여행 경험을 기록하자는 의미를 담았습니다.
- 협력 업체에서 제공 해주는 레저/티켓 상품을 이용한 사용자들은 포스터를 작성할 수 있습니다.
- 포스터를 작성하여 다른 사용자들에게 실질적인 여행 정보를 제공하고, 유용한 여행지를 추천합니다.

---------

# 🔔 핵심 기능
- JWT 기반의 인증/인가
- 예약 및 결제
- Kakao MAP 기반의 장소 검색
- 포스터 작성
- 첨부파일 업로드 및 저장
- 조회 기능 캐싱 최적화

---------

# 📖 기능 설명


---------

# 🛠 기술 스택
<p align="center">
  <img src="https://godekdls.github.io/images/springboot/logo.png" width="100px">
  <img src="https://www.vectorlogo.zone/logos/java/java-icon.svg" width="80px">
  <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA_icon.svg" width="80px">
  <img src="https://www.mysql.com/common/logos/logo-mysql-170x115.png" width="110px">
  <img src="https://developers.kakao.com/tool/resource/static/img/logo/map/kakaomap_vertical_en.png" width="70px" height="90px">
  <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuVjQMv_0hv7zTIQA9GV0VyRzWbtm7yseRYg&s" width="100px">
  <img src="https://upload.wikimedia.org/wikipedia/commons/6/64/Logo-redis.svg" width="130px" height="140px">
  <img src="https://upload.wikimedia.org/wikipedia/commons/9/93/Amazon_Web_Services_Logo.svg" width="110px" height="120px">
  <img src="https://www.docker.com/wp-content/uploads/2022/03/Moby-logo.png" width="110px" height="85px">
</p>

- Spring Boot 3.4.1
- Java 21
- IntelliJ
- MySQL
- GitHub
- Spring Security
- Spring Boot Mail
- Open API
- Redis
- AWS
- Docker

---------

# 👨‍💻 기능별 담당자
|담당자|역할|기능|블로그 주소|
|:----|:----|:----|:----|
|[김지연](https://github.com/jiyeon0926)|리더|1. 장소 검색 <br> 2. Open API 기반 맛집 정보 저장 <br> 3. 예약 및 결제 <br> 4. 맛집 검색 <br> 5. 메일 전송 및 알림 <br>|https://velog.io/@yeoni9094/posts </br> https://blog.naver.com/yeondata|
|[김단빈](https://github.com/kimdanbin)|부리더|1. 포스터 <br> 2. 첨부파일 <br> 3. 댓글 <br> 4. 좋아요 <br> 5. 배포|https://dreamcompass.tistory.com/|
|[안정민](https://github.com/JeongMinAhnn)|팀원|1. JWT <br> 2. 인증/인가 <br> 3. 사용자 CRUD <br> 4. 포스터 검색|https://velog.io/@devtony/posts|
|[이아름](https://github.com/aaahreum)|팀원|1. 협력 업체 CRUD <br> 2. 레저/티켓 상품 CRUD <br> 3. 상품의 일정 CRUD <br> 4. 신고 <br> 5. 조회 기능 캐싱|https://muerha.tistory.com/|

---------

# 💡 Q&A 
<details>
  <summary><b>Q1. Spring 프레임워크를 선택한 이유가 무엇인가요?</b></summary>
  <div markdown="1">
    <p></p>
    <p>
      Spring은 객체 지향 언어가 가진 특징을 최대로 활용할 수 있는 프레임워크 입니다.</br>
      내장 서버(Tomcat 등)를 지원하기 때문에 별도의 웹 서버 설정 없이 실행할 수 있고, 통합 테스트와 단위 테스트를 지원하기 때문에 테스트 가능한 애플리케이션을 구현할 수 있습니다.
    </p>
  </div>
</details>

<details>
  <summary><b>Q2. 객체 지향 언어의 특징은 무엇인가요?</b></summary>
  <div markdown="1">
    <p></p>
    <p>객체 지향 언어의 특징으로 캡슐화, 상속, 추상화, 다형성이 있습니다.</p>
    <li>외부에서 직접 데이터 및 메서드에 접근하지 못하도록 제한할 수 있습니다.</li>
    <li>부모 클래스에 정의된 변수 및 메서드를 자식 클래스에서 상속받아 사용함으로써 코드를 재사용 하거나 재정의 할 수 있습니다.</li>
    <li>공통 기능을 정의하여 여러 클래스에서 일관되게 사용할 수 있습니다.</li>
    <li>동일한 이름의 메서드를 파라미터만 다르게 정의할 수 있고, 상속받은 메서드를 자식 클래스에서 재정의 할 수 있습니다.</li>
  </div>
</details>

<details>
  <summary><b>Q3. MySQL 데이터베이스를 선택한 이유가 무엇인가요?</b></summary>
  <div markdown="1">
    <p></p>
    <p>
      MySQL 데이터베이스는 여러 기업에서 많이 사용하고 있는 오픈 소스 관계형 데이터베이스이며 안정적이고 사용하기 쉽습니다.</br>
      그리고 모든 팀원이 익숙한 데이터베이스이기 때문에 MySQL 데이터베이스를 사용하였습니다.
    </p>
  </div>
</details>

<details>
  <summary><b>Q4. Spring Security를 선택한 이유가 무엇인가요?</b></summary>
  <div markdown="1">
    <p></p>
    <p>
      Spring Security는 Spring 프레임워크에서 애플리케이션의 보안을 관리할 때 사용합니다.</br>
      Spring Security를 적용함으로써 사용자의 인증 및 권한에 따른 인가를 쉽게 관리할 수 있습니다.</br>
      필요에 따라 커스터마이징하여 세부적으로 관리할 수도 있습니다.
    </p>
  </div>
</details>

<details>
  <summary><b>Q5. Redis를 선택한 이유가 무엇인가요?</b></summary>
  <div markdown="1">
    <p></p>
    <p>
      첫 번째 이유는 예약 시스템의 동시성 문제를 해결하기 위해서입니다.</br>
      각 일정에는 인원 수가 제한되어 있고, 같은 일정과 동일한 예약 날짜에 대해 여러 사용자가 동시에 예약 시도를 할 수 있습니다.</br>
      이러한 동시성 문제를 해결하기 위해 Redis를 기반으로 한 Redisson 라이브러리를 활용해 분산 락을 적용했습니다.</br>
      Redisson의 RLock을 사용하면 쉽게 분산 락을 구현할 수 있고, 여러 사용자가 동시에 예약 시도를 해도 최대 인원 제한을 초과하지 않도록 보장할 수 있습니다.
    </p>
    <p>
      두 번째 이유는 조회 기능을 캐싱하기 위해서입니다.</br>
      자주 조회되는 부분을 캐싱하여 응답 속도를 개선함으로써 사용자 및 업체에게 보다 빠른 서비스를 제공할 수 있습니다.</br>
      현재 저희 서비스에서는 데이터 변경이 자주 없는 조회 기능의 첫 번째 페이지를 캐싱하였습니다.
    </p>
  </div>
</details>

<details>
  <summary><b>Q6. 어떤 배포 방법을 선택했나요?</b></summary>
  <div markdown="1">
    <p></p>
    <p>
      GitHub Action을 이용한 CI/CD 방법을 선택했습니다.</br>
      배포 과정에서 Docker와 AWS EC2를 사용합니다.
    </p>
  </div>
</details>
