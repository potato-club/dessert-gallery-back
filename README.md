# 🍰 디저트 갤러리

## 📖 프로젝트 개요 및 구조 설명

### 프로젝트 개요
- 수제 케이크나 맞춤형 디저트를 주문하려면 직접 인스타 등을 통한 예약을 해야 하는 것이 불편하여 개발하게 됨.
- 손님이 원하는 키워드로 가게들을 검색해 정보를 찾아볼 수 있는 웹사이트를 구상함.
- 손님들은 가게 메뉴들을 살펴보며 주문하고 싶은 것이 있으면 팔로우 후 1대1 채팅을 통해 주문함.


<h2>기술 스택 및 구성도</h2>

<h3>ERD 구성도</h3>

![dessert-gallery-erd](https://github.com/potato-club/dessert-gallery-back/assets/84797433/f9221c23-20e3-4b9e-ae59-11937ef2a778)

<br/>

<h3>기술 스택</h3>

- Spring Framework
  - Spring Boot - 스프링 기반 프레임워크
- Amazon Linux 2 + Nginx - 서버 컴퓨터 환경
- MySQL - 기본 데이터베이스
- Redis - 인메모리 기반 데이터베이스
- AWS RDS - 클라우드 데이터베이스
- Spring Data JPA + QueryDsl - Type ORM 기술
- Docker - 배포를 위한 컨테이너 가상화
- Jenkins ( CI / CD ) - 자동화 배포
- Swagger (Springdoc)

<br/>

<h3>각 서비스 역할</h3>

- `Chat Service`
  - WebSocket 을 사용해 채팅 서비스를 지원함.
  - 손님이 가게를 팔로우했을 경우에만 1대1 채팅을 신청할 수 있음.
  - 1대1 채팅을 통해 디저트 예약을 할 수 있음.

- `Mail Service`
  - 로그인 방식 중 자사 로그인 방식에서 사용하는 서비스로 회원가입과 회원탈퇴 취소에서 사용됨.
  - 2차 인증 방식이며 회원가입 때 입력한 Email로 인증 메일을 발송함.
  - 인증 메일에 있는 6자리 인증 번호를 입력하면 인증 완료됨.

- `KakaoMap Service`
  - 해당 가게의 위치가 궁금하거나 내 주변에 어떤 가게가 있는지 찾아볼 때 카카오맵에서 위치를 확인 가능.
  - 지도 맵에서 원하는 위치 좌표를 찍고 검색하면 그 주변의 가게들을 추천해 줌.
  - 도로명 주소를 좌표로 변경해주거나 카카오 맵 안에서 해시태그로 검색 시 가게 리스트와 좌표를 같이 반환함.

- `Follow Service`
  - 구독 서비스로 손님이 가게를 팔로우하는 단방향 기능을 지원함.
  - 팔로우 취소는 손님이나 가게 사장님 둘 중 한 명이 먼저 취소하면 같이 취소됨.
  - 서로 팔로우가 되어 있어야만 1대1 채팅 서비스를 이용 가능함.

- `User Service`
  - 카카오 로그인과 자사 로그인 두 가지를 지원함.
  - SecurityConfig 와 JwtFilter 등이 있는 서비스로 보안 기능을 수행함.
  - 토큰을 발급할 때 Redis에 IP를 함께 받아 저장하면서 보안성을 높임.
  - 마이페이지에서 내 정보 관리, 팔로우 관리, 내가 쓴 글 관리, 채팅 관리, 북마크 관리 등 여러 기능을 포함함.
  - 회원탈퇴 후 7일 간 접속 내역이 없으면 Spring Scheduler 를 통해 관련 데이터를 전부 삭제함.

- `Store Service`
  - Store Service
    - 가게 정보 관련 서비스를 지원함.
    - 사장님은 가게에 대한 생성, 조회, 수정, 삭제가 가능.
    - 일반 회원은 가게에 대한 조회 가능.
    - 가게 일정을 위한 캘린더 서비스 지원, 사용자에 따라 접근 제어 가능.
    - 캘린더 서비스의 조회 API는 사용자 권한에 따라 반환값이 다름.
  - StoreBoard Service
    - 가게의 게시글 관련 서비스를 지원함.
    - 사장님은 가게 게시글을 생성, 조회, 수정, 삭제 가능함.
    - 일반 회원은 가게 게시글 조회만 가능하며 댓글 작성과 북마크 기능을 지원함.
  - StoreList Service
    - 가게 검색 관련 서비스를 지원함.
    - 가게의 위치와 가게 게시글의 해시태그를 통한 가게의 검색 가능
    - 검색 결과에 대한 정렬 기능도 반영.
    - 가게의 리뷰 검색도 가능하며, 가게 정보와 함께 리뷰 정보들도 함께 보여줌.
  - NoticeBoard Service
    - 가게 공지사항에 대한 서비스를 지원함.
    - 사장님이 가게 공지사항을 생성, 수정, 삭제 가능함.
    - 모든 유저는 가게의 공지사항을 조회 가능함.

- `Review Service`
    - 가게에 대한 리뷰 서비스를 지원함.
    - 정상적으로 구매 완료한 유저만 리뷰 작성 가능함.
    - 모든 유저는 리뷰 게시판을 통해 가게에 대한 리뷰 조회 가능.

- `AWS S3 Service`
    - S3 저장소에 사진을 저장하는데 사용하는 서비스로 각 서비스에서 사용 중.
    - 사진을 저장 & 수정 & 삭제 시 데이터베이스의 fileName, fileUrl 컬럼에 반영함.

<br/>

## 🪄 서비스 핵심 기능 요약
### Screen UI

- 로그인 구현
    - 자사 로그인과 카카오 로그인 두 개를 지원함.
    - 자사 로그인 경우 첫 회원가입 때 2차 인증으로 메일 인증을 요구함.
    - 회원가입할 때 손님인지 가게 사장님인지 선택 가능하며 추후 마이페이지 내 정보에서 변경 가능함.

  ![화면 캡처 2023-08-14 200832](https://github.com/potato-club/dessert-gallery-back/assets/84797433/6913c9f6-f124-405a-8a61-b1e5f415226f)

- 메인 페이지
    - 가게 별점을 통한 인기 가게 조회함.
    - 가게 게시글과 리뷰 데이터 반환 시 생성일자 포함해서 최신 데이터 띄움.

  ![메인페이지](https://github.com/potato-club/dessert-gallery-back/assets/84797433/6be20da2-4be6-4011-b32d-f2a05b855636)

- 공지 게시판
    - 가게 공지에 대한 조회 가능.
    - 공지 게시판에는 공지사항인지 이벤트인지 구분해서 작성함.
    - 모든 유저가 공지 게시판 확인 가능함.

  ![공지 게시판](https://github.com/potato-club/dessert-gallery-back/assets/84797433/e473dde4-63de-444c-996d-f0c58e63f97a)

- 가게 게시판
    - 지역과 키워드를 통해 가게 검색 가능함.
    - 지역은 가게의 위치 정보 기반으로 검색.
    - 키워드는 가게 게시글의 해시태그 기반으로 검색.
    - 정렬은 최신순, 평점순, 팔로워순 3가지로 가능함.

  ![가게 게시판](https://github.com/potato-club/dessert-gallery-back/assets/84797433/edae59f7-6cd1-4980-ae04-2cf960d81ae3)

- 게시글 상세보기
    - 가게에서 작성한 게시글의 세부 페이지.
    - 유저들은 댓글 작성 및 북마크 가능.
    - 예약하러 가기 클릭 시 일대일 채팅으로 연결.

  ![게시글 상세보기](https://github.com/potato-club/dessert-gallery-back/assets/84797433/ee991146-8f3d-4260-9792-d2f2bdff4e0e)

- 캘린더
    - 예약/휴무일/이벤트 로 구분해서 일정 등록 가능.
    - 캘린더 일정 조회는 연도와 월 정보를 받아서 보여줌.
    - 가게 페이지에서 볼 수 있는 캘린더와 구분되어 해당 캘린더는 사장님 본인만 모든 일정 확인 가능.

  ![캘린더 관리](https://github.com/potato-club/dessert-gallery-back/assets/84797433/70438326-9208-4ca0-b647-da361bdb6c4a)

- 후기글
    - 일대일 채팅을 통한 거래 완료 후 유저는 후기 작성 가능.
    - 해당 후기 목록들은 가게 후기탭을 통해 조회 가능.

  ![후기글](https://github.com/potato-club/dessert-gallery-back/assets/84797433/28ce6479-7e1d-483a-a1ce-c1ce63937c20)

  ![후기 작성](https://github.com/potato-club/dessert-gallery-back/assets/84797433/c715d1ee-d4de-4ed0-868c-7669db929175)

- 마이페이지 : 내 프로필 설정
    - 내 프로필 정보를 보거나 수정 가능함.
    - 가게 사장님이고 가게를 등록했다면 가게 정보도 출력함.

  ![마이페이지](https://github.com/potato-club/dessert-gallery-back/assets/84797433/0edb883a-e21a-4d34-97ae-e5b127e16f94)

- 마이페이지 : 팔로우 관리
    - 팔로우 한 가게 or 손님들 목록을 한 페이지 당 15개씩 보여줌.
    - 한 쪽에서 먼저 팔로우를 취소하면 같이 취소됨.
    - 팔로우 신청은 가게 메인페이지에서 팔로우 버튼을 누르면 맺어짐.

  ![팔로우](https://github.com/potato-club/dessert-gallery-back/assets/84797433/f2af2619-6dc0-4e8e-9250-1011792e6356)

- 마이페이지 : 1대1 채팅
    - 팔로우가 정상적으로 맺어졌다면 이용 가능한 서비스.
    - 팔로우를 맺고 오른쪽 위 더 보기 창에서 채팅 버튼을 누르면 채팅방이 생성됨.
    - 한 쪽에서 채팅방을 나오게 되면 같이 나와지고 방을 삭제함.
    - 채팅 내역은 1달동안 보관되고 1달이 지난 내역은 Spring Scheduler 가 자동으로 삭제함.

  ![1대1 채팅](https://github.com/potato-club/dessert-gallery-back/assets/84797433/82f4f884-ccde-42ba-9fe5-8741aa8b8f51)

- 마이페이지 : 북마크
    - 유저는 가게 게시글에 대해 북마크 등록 가능.
    - 마이페이지를 통해 본인이 북마크 등록한 게시글 목록 조회 가능.

  ![북마크](https://github.com/potato-club/dessert-gallery-back/assets/84797433/8f16c5c8-0230-41fc-b96a-a886e23674be)

- 마이페이지 : 내가 쓴 글
    - 유저 본인이 작성한 후기 목록 조회 가능.
    - 작성한 후기에 대한 수정이나 삭제 가능.

  ![내가 쓴 글](https://github.com/potato-club/dessert-gallery-back/assets/84797433/725af2ac-eba7-4dc2-ac57-83a670563f5e)

- 카카오 맵
    - 카카오 맵을 이용할 수 있는 방법은 메인 페이지에서 바로 카카오 맵으로 이동하는 것과 가게 메인 페이지에서 카카오 맵으로 가는 것 총 2가지다.
    - 전자는 미리 살펴볼 지역을 모달로 받아 해당 지역의 한 지점으로부터의 주변 가게들 리스트를 반환함.
    - 후자는 미리 특정된 좌표가 있기 때문에 해당 가게의 위치와 주변 가게들의 리스트를 반환함.
    - 옆의 검색으로 해시태그 등을 입력해 원하는 가게들을 검색할 수 있고 리스트의 가게를 누르면 상세 정보와 위치가 나타남.

  ![카카오맵](https://github.com/potato-club/dessert-gallery-back/assets/84797433/e5af4672-4d9c-48a1-bed9-b7aa4b32c4cd)

<br/>

<h2>👍 협업한 팀원들 소개</h2>

**협업한 프론트 팀의 깃허브 주소** : [https://github.com/potato-club/dessert-gallery-front](https://github.com/potato-club/dessert-gallery-front)


**디자이너 장보경 님 Figma 주소** : [Figma](https://www.figma.com/file/Q0OF8paagt92QrSjkeYuwZ/%EB%94%94%EC%A0%80%ED%8A%B8-%EA%B0%A4%EB%9F%AC%EB%A6%AC?type=design&node-id=969%3A32&mode=design&t=TmgNGkUGw3Wpb6Ai-1)
