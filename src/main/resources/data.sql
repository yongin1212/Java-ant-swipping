INSERT INTO members (login_id, password, name, role, created_at, updated_at) VALUES
('user1', '$2a$10$h5xxHuakGGKLkoItaXXml.0HYjxCySpJAcUsJU/73tniMtg5KbN1u', '일반회원1', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user2', '$2a$10$h5xxHuakGGKLkoItaXXml.0HYjxCySpJAcUsJU/73tniMtg5KbN1u', '일반회원2', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user3', '$2a$10$h5xxHuakGGKLkoItaXXml.0HYjxCySpJAcUsJU/73tniMtg5KbN1u', '일반회원3', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin', '$2a$10$h5xxHuakGGKLkoItaXXml.0HYjxCySpJAcUsJU/73tniMtg5KbN1u', '관리자', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO stocks (code, name, sector, current_price, created_at, updated_at) VALUES
('000001', '한빛전자', 'SEMICONDUCTOR', 10000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('000002', '새봄반도체', 'SEMICONDUCTOR', 20000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('000003', '미래소프트', 'IT', 15000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('000004', '구름게임즈', 'IT', 8000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('000005', '푸른자동차', 'AUTOMOBILE', 30000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('000006', '새빛에너지', 'ENERGY', 12000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO market_events (title, description, target_type, target_stock_id, target_sector, change_rate, status, created_at, updated_at) VALUES
('신제품 흥행', '한빛전자 신제품 판매 증가', 'STOCK', 1, NULL, 10, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('반도체 수요 증가', '반도체 업종 수요 증가', 'SECTOR', NULL, 'SEMICONDUCTOR', 8, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('시장 투자심리 악화', '전체 시장 투자심리 악화', 'ALL', NULL, NULL, -5, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (member_id, initial_cash, cash, created_at, updated_at)
SELECT id, 10000000, 10000000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM members ORDER BY id;
