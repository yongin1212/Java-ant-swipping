# 매매, 계좌 API (B 담당)

모든 API는 HTTP Basic 인증이 필요하다. 회원 ID는 요청에 넣지 않고 로그인 사용자에서 꺼낸다.
응답은 공통 형식 `{ "success", "data", "error" }`을 따른다.

```
Authorization: Basic dXNlcjE6cGFzc3dvcmQ=   # user1:password
```

## 계산식

```
주식 평가금액 = 현재가 × 보유 수량
총자산       = 현금 + 전체 주식 평가금액
손익         = 총자산 - 초기 투자금
수익률(%)    = 손익 ÷ 초기 투자금 × 100   (소수점 둘째 자리 반올림)
```

금액은 모두 `BigDecimal`, 원 단위(scale 0)다. 체결 가격은 요청값이 아닌 서버의 현재가다.

## API 목록

| 메서드 | URI | 설명 | 성공 | 실패 |
|---|---|---|---|---|
| POST | `/api/trades/buy` | 매수 | 201 | 400 `INVALID_INPUT`, 400 `INSUFFICIENT_CASH`, 404 `STOCK_NOT_FOUND` |
| POST | `/api/trades/sell` | 매도 | 201 | 400 `INVALID_INPUT`, 400 `INSUFFICIENT_QUANTITY`, 404 `STOCK_NOT_FOUND`, 404 `HOLDING_NOT_FOUND` |
| GET | `/api/trades` | 내 거래 내역 (최신순) | 200 | — |
| GET | `/api/account` | 초기 투자금, 현금 잔액 | 200 | 404 `ACCOUNT_NOT_FOUND` |
| GET | `/api/account/holdings` | 보유 주식별 수량, 현재가, 평가금액 | 200 | 404 `ACCOUNT_NOT_FOUND` |
| GET | `/api/account/assets` | 총자산, 손익, 수익률 | 200 | 404 `ACCOUNT_NOT_FOUND` |

로그인하지 않으면 모두 401 `UNAUTHORIZED`.

## 매수 / 매도

```http
POST /api/trades/buy
Content-Type: application/json

{ "stockId": 1, "quantity": 10 }
```

- `stockId`: 필수, 양수
- `quantity`: 필수, 1 이상 1,000,000 이하 정수

```json
{
  "success": true,
  "data": {
    "tradeId": 1,
    "stockId": 1,
    "stockName": "한빛전자",
    "type": "BUY",
    "quantity": 10,
    "price": 10000,
    "totalAmount": 100000,
    "tradedAt": "2026-11-19T09:36:54.313461"
  },
  "error": null
}
```

매도(`/api/trades/sell`)도 요청, 응답 형식이 같고 `type`이 `SELL`이다. 전량 매도하면 보유 주식 목록에서 빠진다.

처리 순서 (하나의 트랜잭션, 중간 실패 시 전체 롤백):

- 매수: 종목 잠금 → 계좌 잠금 → 현재가로 총액 계산 → 현금 차감 → 보유 수량 증가 → 거래 기록 저장
- 매도: 종목 잠금 → 계좌 잠금 → 보유 주식 잠금 → 수량 감소 → 현금 증가 → 거래 기록 저장

## 거래 내역

```http
GET /api/trades
```

```json
{
  "success": true,
  "data": [
    { "tradeId": 3, "stockId": 3, "stockName": "미래소프트", "type": "SELL", "quantity": 5,
      "price": 15000, "totalAmount": 75000, "tradedAt": "2026-11-19T09:38:03.356604" },
    { "tradeId": 2, "stockId": 3, "stockName": "미래소프트", "type": "BUY", "quantity": 20,
      "price": 15000, "totalAmount": 300000, "tradedAt": "2026-11-19T09:38:03.276779" }
  ],
  "error": null
}
```

## 계좌, 보유 주식, 총자산

```http
GET /api/account
```

```json
{ "success": true, "data": { "accountId": 1, "initialCash": 10000000, "cash": 9675000 }, "error": null }
```

```http
GET /api/account/holdings
```

```json
{
  "success": true,
  "data": [
    { "stockId": 1, "stockCode": "000001", "stockName": "한빛전자", "quantity": 10,
      "currentPrice": 11000, "evaluationAmount": 110000 }
  ],
  "error": null
}
```

```http
GET /api/account/assets
```

```json
{
  "success": true,
  "data": {
    "initialCash": 10000000,
    "cash": 9675000,
    "stockEvaluation": 335000,
    "totalAsset": 10010000,
    "profit": 10000,
    "returnRate": 0.10
  },
  "error": null
}
```

## 동시 매매 처리

매매와 이벤트 적용이 같은 종목을 동시에 건드릴 수 있어 비관적 잠금(`PESSIMISTIC_WRITE`)을 쓴다.
잠금 순서는 항상 **Stock → Account → Holding**으로, 이벤트 적용(C)과 같은 순서라 데드락이 생기지 않는다.
체결가는 이벤트 적용 전 가격이나 적용 후 가격 중 하나로만 정해진다.

| 쿼리 | 방식 | 용도 |
|---|---|---|
| `StockRepository.findByIdForUpdate` (C) | `@Lock` + JPQL | 매매 대상 종목 잠금 |
| `AccountRepository.findByMemberIdForUpdate` | `@Lock` + JPQL | 잔액 동시 변경 방지 |
| `HoldingRepository.findByAccountIdAndStockIdForUpdate` | `@Lock` + JPQL | 보유 수량 동시 변경 방지 |
| `HoldingRepository.findAllWithStockByAccountId` | JPQL `JOIN FETCH` | 보유 주식 + 현재가 한 번에 조회 (N+1 방지) |
| `TradeRepository.findAllByAccountIdOrderByTradedAtDescIdDesc` | Query Method + `@EntityGraph` | 거래 내역 최신순 + 종목명 |
| `AccountRepository.findByMemberId` | Query Method | 내 계좌 조회 |

## 테스트

`@SpringBootTest` + `@Transactional`로 실제 Service와 H2 DB를 사용하고, 테스트가 끝나면 데이터가 되돌아간다.

| 클래스 | 종류 | 테스트 | 정상/예외 |
|---|---|---|---|
| `TradeControllerTest` | Controller (MockMvc) | 매수 성공 201 | 정상 |
| | | 수량 0 매수 400 `INVALID_INPUT` | 예외 |
| | | 로그인 없이 매수 401 | 예외 |
| `TradeServiceTest` | Service (JUnit, AssertJ) | 매수 성공, 현금 감소 | 정상 |
| | | 현금 부족 매수 `INSUFFICIENT_CASH` | 예외 |
| | | 매도 성공, 현금 증가 | 정상 |
| | | 보유 수량 초과 매도 `INSUFFICIENT_QUANTITY` | 예외 |
| `AccountServiceTest` | Service (JUnit, AssertJ) | 주가 상승 후 총자산, 손익, 수익률 | 정상 |
| | | 계좌 없는 회원 조회 `ACCOUNT_NOT_FOUND` | 예외 |

## 초기 데이터

[`data.sql`](../src/main/resources/data.sql)에서 회원을 먼저 등록하고 각 회원의 계좌를 초기 투자금 10,000,000원으로 만든다.
