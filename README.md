![AhubBE Architecture](https://github.com/user-attachments/assets/b289422b-c12d-4655-ae71-5b733f200e98)


## Tech Stack
<img src="https://img.shields.io/badge/spring-6DB33F?style=flat-square&logo=spring&logoColor=white"/> <img src="https://img.shields.io/badge/springboot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=flat-square&logo=springsecurity&logoColor=white"/>
<img src="https://img.shields.io/badge/hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white"/>
<img src="https://img.shields.io/badge/mariadb-003545?style=flat-square&logo=mariadb&logoColor=white"/>
<img src="https://img.shields.io/badge/h2database-09476B?style=flat-square&logo=h2database&logoColor=white"/>
<img src="https://img.shields.io/badge/swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white"/>
<img src="https://img.shields.io/badge/githubactions-2088FF?style=flat-square&logo=githubactions&logoColor=white"/>
## Dependencies
| Category | Library | Version | Description |
| :--- | :--- | :--- | :--- |
| **Framework** | **Spring Framework** | 7.0.2 | IoC/DI 컨테이너 및 핵심 프로그래밍 모델의 근간 제공 |
| **Framework** | **Spring Boot** | 4.0.1 | 프로젝트 전반의 자동 설정 및 내장 서버 제공 |
| **Language** | **Java** | 17 (LTS) | 현대적인 자바 문법과 안정적인 런타임 환경 제공 |
| **Security** | **Spring Security** | - | 인증(Authentication) 및 인가(Authorization) 처리를 위한 보안 프레임워크 |
| **Auth (JWT)** | **JJWT** | 0.11.5 | 무상태(Stateless) 서버를 위한 JWT 토큰 생성 및 검증 라이브러리 |
| **Persistence** | **Spring Data JPA** | - | 인터페이스 기반의 데이터 접근 계층 구현 및 ORM 지원 |
| **Database** | **MariaDB** | - | 상용 환경의 메인 관계형 데이터베이스 (Connector/J 활용) |
| **Test DB** | **H2 Database** | - | 테스트 코드 및 로컬 개발 환경용 인메모리 데이터베이스 |
| **API Docs** | **Springdoc OpenAPI** | 2.2.0 | Swagger UI를 통한 대화형 API 명세서 자동 생성 및 테스트 |
| **Mail** | **Spring Mail** | - | Google SMTP 연동을 통한 이메일 발송 기능 지원 |
| **Crawler** | **JSoup** | 1.21.2 | 외부 웹 데이터 추출을 위한 HTML 파싱 및 조작 라이브러리 |
| **Productivity** | **Lombok** | - | Getter, Setter 등 반복되는 상용구 코드 자동 생성 |
| **Validation** | **Validation** | - | DTO 객체의 필드값 유효성 검증 라이브러리 |
| **Code Quality** | **JaCoCo** | 0.8.12 | 테스트 코드 실행 결과 및 코드 커버리지 리포트 생성 도구 |
| **Lint / Format** | **Spotless** | 6.23.3 | Google Java Format 기반 코드 스타일 자동 정렬 및 검사 |

## Page Overview
<table style="width:100%; text-align:center; vertical-align:middle;">
  <tr>
    <th>랜딩 페이지</th>
  </tr>
  <tr>
    <td><img width="1708" height="1284" alt="Landing" src="https://github.com/user-attachments/assets/ec16047c-9d26-465f-bca8-5d737d1ca77b" /></td>
  </tr>
</table>
<table style="width:100%; text-align:center; vertical-align:middle;">
  <tr>
    <th>로그인 페이지
    <th>회원가입 페이지
    <th>비밀번호 초기화 모달
  </tr>
  <tr>
    <td><img width="1699" height="1243" alt="화면 캡처 2026-01-07 210224" src="https://github.com/user-attachments/assets/e22fee44-cdf4-4f33-8722-e66b73cc0aa7" /></td>
    <td><img width="1699" height="1293" alt="image" src="https://github.com/user-attachments/assets/2453a27c-24f0-481a-ab13-4c2dad31e19c" /></td>
    <td><img width="1712" height="1291" alt="image" src="https://github.com/user-attachments/assets/4fdadf51-dc12-4f91-92c6-d7ca078d6158" /></td>
  </tr>
</table>
<table style="width:100%; text-align:center; vertical-align:middle;">
  <tr>
    <th>메인 페이지
  </tr>
  <tr>
    <td><img width="1703" height="1300" alt="image" src="https://github.com/user-attachments/assets/d00500fe-5820-4f9b-bd4a-34f718361adc" /></td>
  </tr>
</table>
<table style="width:100%; text-align:center; vertical-align:middle;">
  <tr>
    <th>세부 정보 페이지
  </tr>
  <tr>
    <td><img width="1702" height="1297" alt="image" src="https://github.com/user-attachments/assets/161730af-3b10-4154-9e76-1c7396bbb5c6" /></td>
  </tr>
</table>
<table style="width:100%; text-align:center; vertical-align:middle;">
  <tr>
    <th>탐색 페이지
  </tr>
  <tr>
    <td><img width="1710" height="1296" alt="image" src="https://github.com/user-attachments/assets/6a4be8ff-d079-436a-8195-30bd9ae02418" /></td>
  </tr>
</table>
<table style="width:100%; text-align:center; vertical-align:middle;">
  <tr>
    <th>개인 설정 페이지
    <th>개인 애니메이션 페이지
  </tr>
  <tr>
    <td><img width="1677" height="1289" alt="image" src="https://github.com/user-attachments/assets/0d09adc3-93b7-4650-b7ad-d8e71eb7cb65" /></td>
     <td><img width="1694" height="1302" alt="image" src="https://github.com/user-attachments/assets/b981df3f-00ad-4d73-a17b-25eaaeda1a4b" /></td>
  </tr>
  </tr>
</table>
<table style="width:100%; text-align:center; vertical-align:middle;">
  <tr>
    <th colspan="2" style="background-color: #f8f9fa; font-size: 1.2em; padding: 10px; border-right: 1px solid #ddd;">
      비밀번호 재설정 모달
    </th>
    <th colspan="1" style="background-color: #fff0f0; font-size: 1.2em; padding: 10px; color: #d9534f;">
      회원 탈퇴 모달
    </th>
  </tr>
  <tr>
    <th style="width:33%;">비밀번호 인증</th>
    <th style="width:33%;">비밀번호 재설정</th>
    <th style="width:34%;">회원 탈퇴</th>
  </tr>
  <tr>
    <td>
      <img width="100%" alt="비밀번호 인증" src="https://github.com/user-attachments/assets/33ee1bd7-245f-40b9-8763-139d8ba94243" />
    </td>
    <td>
      <img width="100%" alt="비밀번호 재설정" src="https://github.com/user-attachments/assets/9f527e4a-4aea-4c51-9816-c22ed650f045" />
    </td>
    <td>
      <img width="100%" alt="회원 탈퇴" src="https://github.com/user-attachments/assets/2d559812-c72d-4a64-9ee7-6565f593bd4b" />
    </td>
  </tr>
</table>
