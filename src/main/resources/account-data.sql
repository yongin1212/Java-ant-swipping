-- 회원별 계좌 (초기 투자금 10,000,000원). member-data.sql 이후에 실행되어야 한다.
INSERT INTO accounts (member_id, initial_cash, cash, created_at, updated_at)
SELECT id, 10000000, 10000000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM members ORDER BY id;
