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
<details>
  <summary><b>인증/인가</b></summary>
  <div markdown="1">
    <p></p>
    <li>사용자(USER), 협력 업체(PARTNER), 관리자(ADMIN) 권한으로 나눠 관리합니다.</li>
    <li>USER : 일반 사용자 권한</li>
    <li>PARTNER : 업체 관련 기능을 수행할 수 있는 권한</li>
    <li>ADMIN : 관리자 권한, 시스템 전체를 관리</li>
    <li>계층 구조 : USER < PARTNER < ADMIN</li>
    <li>사용자가 권한을 선택하여 가입할 수 없도록 사용자와 관리자의 회원 가입 URL을 분리하였습니다.</li>
    <li>관리자가 직접 검증하여 업체 정보를 등록하면 해당 업체 관리자는 PARTNER 권한을 받을 수 있습니다.</li>
    <li>로그인을 하면 Access Token과 Refresh Token을 발급 받습니다.</li>
    <li>사용자 정보가 Token에 담겨 있기 때문에 Token을 통해 인가 처리를 할 수 있습니다.</li>
    <li>주로 검색, 조회 기능은 화이트 리스트에 담겨 있기 때문에 인증 없이 접속할 수 있습니다.</li>
  </div>
</details>

<details>
  <summary><b>레저/티켓 상품 및 일정</b></summary>
  <div markdown="1">
    <p></p>
    <li>레저/티켓 상품은 PARTNER 권한을 가진 협력 업체와 ADMIN 권한을 가진 관리자만 등록할 수 있습니다.</li>
    <li>업체는 상품을 등록한 후, 그 상품에 대한 일정을 등록해야 합니다.</li>
    <li>일정은 해당 상품을 이용할 수 있는 시간과 인원수 정보를 가집니다.</li>
    <li>업체가 등록할 수 있는 상품 및 일정 개수는 제한이 없습니다.</li>
    <li>일정의 최대 인원수는 999로 제한이 있습니다.</li>
  </div>
</details>

<details>
  <summary><b>예약</b></summary>
  <div markdown="1">
    <p></p>
    <li>사용자는 상품의 판매 기간 동안 예약 신청을 할 수 있습니다.</li>
    <li>상품의 판매 기간이 2월 3일부터 2월 5일까지라면 예약 날짜는 2월 3일, 4일, 5일 중 선택해 신청해야 합니다.</li>
    <li>당일 예약을 할 수 있으나 현재 시간이 오픈 시간을 지날 경우, 예약이 불가능합니다.</li>
    <li>사용자는 일정, 예약 날짜, 인원 수를 반드시 선택해야 합니다.</li>
    <li>동일한 예약 날짜가 존재할 경우, 중복 예약으로 간주되어 예약이 불가능합니다.</li>
    <li>사용자는 예약 취소를 할 수 있습니다.</li>
    <li>사용자는 예약을 신청할 수 있지만, 이는 아직 확정되지 않은 예약입니다.</li>
    <li>업체는 예약을 승인하거나 거절할 수 있으며, 예약 승인이 이루어져야 예약이 확정됩니다.</li>
    <li>예약 승인일로부터 다음 날 오후 6시까지 결제가 이루어지지 않으면 예약은 자동으로 만료 처리됩니다.</li>
  </div>
</details>

<details>
  <summary><b>결제</b></summary>
  <div markdown="1">
    <p></p>
    <li>승인이 된 예약만 결제할 수 있습니다.</li>
    <li>사용자는 결제 내역을 조회할 수 있습니다.</li>
    <li>사용자는 결제 취소를 할 수 있습니다.</li>
    <li>당일 취소는 불가하며 전 날까지 취소할 경우, 환불 받을 수 있습니다.</li>
    <li>상업용 서비스가 아니기 때문에 자체적으로 환불 정책을 만들어서 적용했습니다.</li>
    <li>예약 전날 환불 시 30% 환불이 가능하며, 전날 환불이 아닐 경우에는 100% 환불이 가능합니다.</li>
    <li>결제 취소가 이루어지더라도 예약 취소는 자동으로 처리되지 않습니다.</li>
    <li>APPROVED : 결제 완료</li>
    <li>CANCELED : 결제 취소</li>
    <li>NO_REFUND : 환불 안 함</li>
    <li>PARTIAL_REFUND : 부분 환불</li>
    <li>FULL_REFUND : 전액 환불</li>
  </div>
</details>

<details>
  <summary><b>장소 검색</b></summary>
  <div markdown="1">
    <p></p>
    <li>특정 키워드로 검색하면 관련된 장소가 Kakao MAP에서 제공하는 데이터에 기반해 검색됩니다.</li>
    <li>카테고리, 장소명, 전화번호, 주소, 경도 및 위도 정보를 받을 수 있습니다.</li>
  </div>
</details>

<details>
  <summary><b>포스터</b></summary>
  <div markdown="1">
    <p></p>
    <li>레저/티켓 상품을 이용한 사용자만 포스터를 작성할 수 있습니다.</li>
    <li>사용자의 레저/티켓 상품 이용 여부는 결제 상태를 기준으로 판단합니다.</li>
    <li>이미지 및 영상을 같이 첨부해 포스터를 작성할 수 있습니다.</li>
    <li>사용자는 서버에 저장된 맛집 정보를 기반으로 포스터에 추천할 맛집을 선택할 수 있습니다.</li>
    <li>포스터에 작성된 내용을 바탕으로 검색할 수 있으며, 이를 통해 유용한 여행지를 추천받을 수 있습니다.</li>
  </div>
</details>

<details>
  <summary><b>댓글</b></summary>
  <div markdown="1">
    <p></p>
    <li>포스터에 댓글을 작성할 수 있습니다.</li>
    <li>댓글을 수정할 수 있습니다.</li>
    <li>댓글을 삭제할 수 있습니다.</li>
    <li>대댓글 기능은 없습니다.</li>
  </div>
</details>

<details>
  <summary><b>신고</b></summary>
  <div markdown="1">
    <p></p>
    <li>포스터를 신고할 수 있습니다.</li>
    <li>댓글을 신고할 수 있습니다.</li>
    <li>신고 이유를 적어야 합니다.</li>
    <li>이미 신고한 포스터 또는 댓글을 2번 이상 신고할 수 없습니다.</li>
    <li>신고 당한 횟수가 5회일 경우, 자동으로 삭제 처리 됩니다.</li>
  </div>
</details>

<details>
  <summary><b>메일 전송 및 알림</b></summary>
  <div markdown="1">
    <p></p>
    <li>예약 관련해 사용자와 업체는 메일을 받을 수 있습니다.</li>
    <li>메일이 전송 되면 알림 테이블에 데이터가 저장 됩니다.</li>
    <li>사용자와 업체는 본인이 받은 알림 내역을 조회할 수 있습니다.</li>
    <li>30일 지난 알림은 자동으로 삭제 처리 됩니다.</li>
  </div>
</details>

<details>
  <summary><b>좋아요</b></summary>
  <div markdown="1">
    <p></p>
    <li>포스터를 좋아요 할 수 있습니다.</li>
    <li>댓글을 좋아요 할 수 있습니다.</li>
    <li>좋아요를 취소할 수 있습니다.</li>
  </div>
</details>

---------

# 👨‍💻 기능별 담당자
|담당자|역할|기능|블로그 주소|
|:----|:----|:----|:----|
|[김지연](https://github.com/jiyeon0926)|리더|1. 장소 검색 <br> 2. Open API 기반 맛집 정보 저장 <br> 3. 예약 및 결제 <br> 4. 맛집 검색 <br> 5. 메일 전송 및 알림 <br>|https://velog.io/@yeoni9094/posts </br> https://blog.naver.com/yeondata|
|[김단빈](https://github.com/kimdanbin)|부리더|1. 포스터 <br> 2. 첨부파일 <br> 3. 댓글 <br> 4. 좋아요 <br> 5. 배포|https://dreamcompass.tistory.com/|
|[안정민](https://github.com/JeongMinAhnn)|팀원|1. JWT <br> 2. 인증/인가 <br> 3. 사용자 CRUD <br> 4. 포스터 검색|https://velog.io/@devtony/posts|
|[이아름](https://github.com/aaahreum)|팀원|1. 협력 업체 CRUD <br> 2. 레저/티켓 상품 CRUD <br> 3. 상품의 일정 CRUD <br> 4. 신고 <br> 5. 조회 기능 캐싱|https://muerha.tistory.com/|

---------

# 🧩 설계

### 1️⃣ 와이어프레임
👉 [와이어프레임](https://embed.figma.com/design/YbndMHkYnziJryWMkw9k9p/6%EC%A1%B0?node-id=0-1&t=t6jrbnQBf3RPx4pb-1&embed-host=notion&footer=false&theme=system)

### 2️⃣ API 명세서
👉 [API 명세서](https://github.com/kimdanbin/travel-shooting/wiki/API-%EB%AA%85%EC%84%B8%EC%84%9C)

### 3️⃣ ERD
<img src="https://github.com/user-attachments/assets/b754e2cd-7728-4728-b23a-3021da94547d">

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

# ⚙ 아키텍처 구조
<img src="https://github.com/user-attachments/assets/d6fb7078-10f8-44fc-b787-c11884aeea84">
