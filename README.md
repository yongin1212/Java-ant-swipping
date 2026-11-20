# 개미털기

이벤트에 따라 바뀌는 가상 주가로 매수·매도하고, 관심종목과 투자 메모를 관리하는 REST API입니다.

## 실행 환경

- Java 21, Spring Boot 4.1.1
- H2 인메모리 데이터베이스
- 저장소의 Gradle Wrapper 사용
- 별도 외부 API 키나 필수 환경 변수 없음

JDK 21을 설치하고 `JAVA_HOME` 또는 IDE의 Gradle JVM을 JDK 21로 설정합니다. 아래 명령은 `build.gradle`이 있는 프로젝트 폴더에서 실행합니다.

```bash
./gradlew clean build
./gradlew bootRun
```

Windows에서는 각각 `gradlew.bat clean build`, `gradlew.bat bootRun`을 사용합니다. 기본 주소는 `http://localhost:8080`입니다. 8080 포트를 이미 사용한다면 다음처럼 바꿀 수 있습니다.

```bash
./gradlew bootRun --args='--server.port=8081'
```

## 초기 데이터와 인증

앱을 시작하면 [data.sql](src/main/resources/data.sql)이 회원, 종목, 이벤트, 계좌를 생성합니다.

| 계정 | 비밀번호 | 권한 |
|---|---|---|
| `user1` | `password` | USER |
| `user2` | `password` | USER |
| `user3` | `password` | USER |
| `admin` | `password` | ADMIN |

초기 데이터는 회원 4명, 종목 6개, 미적용 이벤트 3개, 계좌 4개입니다. 각 계좌의 초기 투자금과 잔액은 10,000,000원이며, 관심종목·메모·거래·보유주식은 비어 있습니다. 앱을 종료하고 다시 실행하면 데이터가 초기 상태로 돌아옵니다.

인증은 HTTP Basic을 사용합니다. 별도 로그인 API나 토큰 발급 없이 매 요청에 계정과 비밀번호를 보냅니다.

```bash
curl -u user1:password http://localhost:8080/api/auth/me
curl -u user1:password http://localhost:8080/api/stocks
curl -u user1:password http://localhost:8080/api/account
```

일반 회원의 관심종목과 메모는 본인 데이터만 조회·수정·삭제할 수 있습니다. `/api/admin/**`는 `admin` 계정으로 요청합니다.

## A 기능 실행 예시

IntelliJ HTTP Client에서 예시 파일을 열고 위에서부터 순서대로 실행합니다. 포트를 바꿨다면 첫 줄의 `baseUrl`을 수정합니다. [API 예시](http/a-api.http)

예제는 관심종목에 추가할 수 있는 종목을 골라 등록하고, 서버가 반환한 ID로 수정·삭제까지 진행합니다. 이미 모든 종목을 등록한 계정이라면 파일의 `username`을 다른 데모 계정으로 바꿔 실행합니다.

| 기능 | 메서드와 경로 | 요청 |
|---|---|---|
| 관심종목 등록 | `POST /api/watchlist` | `stockId`, 선택값 `targetPrice` |
| 관심종목 목록 | `GET /api/watchlist` | 없음 |
| 목표 가격 수정 | `PUT /api/watchlist/{id}` | `targetPrice` |
| 관심종목 삭제 | `DELETE /api/watchlist/{id}` | 없음 |
| 메모 등록 | `POST /api/memos` | `stockId`, `title`, `content` |
| 메모 목록·검색 | `GET /api/memos` | 선택 쿼리 `keyword`, `stockId` |
| 메모 한 건 조회 | `GET /api/memos/{id}` | 없음 |
| 메모 수정 | `PUT /api/memos/{id}` | `title`, `content` |
| 메모 삭제 | `DELETE /api/memos/{id}` | 없음 |

메모의 `keyword`는 제목에 포함된 문자열을 찾고, `stockId`는 종목을 지정합니다. QueryDSL 쿼리에 로그인한 회원 ID를 먼저 넣고 입력한 조건만 추가합니다. 이 방식으로 네 가지 조건 조합을 한 쿼리 메서드로 처리합니다. 두 조건을 함께 보내면 둘 다 만족하는 본인 메모만 반환합니다.

목표 가격은 원 단위 양수이며 소수점은 허용하지 않습니다. 선택값이므로 `null`을 허용하고, 수정 시 `null`을 보내거나 값을 생략하면 목표 가격이 지워집니다. 메모 제목은 50자, 내용은 1,000자까지 입력할 수 있으며 공백만 있는 값은 허용하지 않습니다. 수정 API로 소유자나 종목을 바꾸지는 않습니다.

성공 응답은 `success`, `data`, `error` 필드를 사용합니다. 등록은 `201`, 조회·수정은 `200`, 삭제는 본문 없는 `204`입니다. 잘못된 입력은 `400`, 인증 실패는 `401`, 타인 데이터 접근은 `403`, 없는 데이터는 `404`, 관심종목 중복 등록은 `409`입니다.

## 팀 API 연결

계좌·보유주식·자산·매매·거래 내역의 요청과 응답은 [계좌·매매 API 문서](docs/trade-account-api.md)를 참고합니다. 체결 가격은 서버의 현재가를 사용하며, 이벤트를 적용하면 대상 종목의 현재가가 바뀝니다.

## H2와 테스트

JPA가 테이블을 만든 뒤 `data.sql`을 실행하도록 `defer-datasource-initialization`을 사용합니다. 일반 앱 실행과 `@SpringBootTest`가 같은 초기 데이터 설정을 사용합니다. 테스트에는 고정된 H2 DB 이름을 지정하지 않아 별도 애플리케이션 컨텍스트가 같은 인메모리 DB를 공유하지 않도록 합니다.

기본 H2 JDBC URL은 실행할 때 자동 생성됩니다. 서버 시작 로그에서 `jdbc:h2:mem:`이 포함된 줄로 확인할 수 있습니다. 수동 실행에서만 알아보기 쉬운 이름이 필요하다면 다음처럼 지정할 수 있습니다.

```bash
./gradlew bootRun --args='--spring.datasource.url=jdbc:h2:mem:antdemo'
```

현재 설정은 H2 웹 콘솔을 활성화하지 않으므로 `/h2-console` 접속을 실행 절차에 포함하지 않습니다. 인메모리 DB는 앱 프로세스 안에 있으며, 데이터 확인은 위 API와 자동화 테스트로 진행합니다.

테스트 결과는 `build/reports/tests/test/index.html`에서 확인할 수 있습니다.
