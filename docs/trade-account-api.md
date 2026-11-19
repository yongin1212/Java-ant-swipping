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

`TradeConcurrencyTest`에서 종목, 계좌 잠금을 빼고 돌리면 두 테스트가 모두 실패한다.

- 동시 매수: 같은 계좌, 종목의 보유 행이 두 번 만들어져 unique 제약 위반
- 동시 매도: 3건이 체결됐는데 잔액이 매도 금액만큼 늘지 않음 (동시에 읽은 잔액을 각자 저장해 앞선 변경이 덮어써지는 lost update)

잠금을 넣으면 10개 동시 요청 중 살 수 있는 만큼(5건)만 체결되고 잔액은 0원에서 멈춘다.

## 테스트

| 클래스 | 종류 | 내용 |
|---|---|---|
| `TradeServiceTest` | Service | 매수 성공, 누적 매수, 잔액 부족, 없는 종목, 매도 성공, 전량 매도, 수량 부족, 미보유, 거래 내역 최신순 |
| `AccountServiceTest` | Service | 시세 변경 후 총자산, 손익, 수익률, 손실 반올림, 보유 주식, 계좌 없음 |
| `TradeRollbackTest` | `@SpringBootTest` | 거래 기록 저장 실패 시 현금, 보유 수량 롤백 |
| `TradeConcurrencyTest` | `@SpringBootTest` | 동시 매수, 동시 매도에서 음수 방지 |
| `TradeControllerTest` | MockMvc | 201, 400, 404, 401 응답과 조회 API |

## 초기 데이터

`src/main/resources/account-data.sql`: 회원마다 초기 투자금 10,000,000원 계좌를 만든다. `member-data.sql` 다음에 실행되어야 한다.
