package kr.hs.dgsw.javasuhaeng.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_EVENT_TARGET(HttpStatus.BAD_REQUEST, "이벤트 적용 대상과 입력값 조합이 올바르지 않습니다."),
    INSUFFICIENT_CASH(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    INSUFFICIENT_QUANTITY(HttpStatus.BAD_REQUEST, "보유 수량이 부족합니다."),

    // 401, 403
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 404
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다."),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "종목을 찾을 수 없습니다."),
    WATCHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "관심종목을 찾을 수 없습니다."),
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "메모를 찾을 수 없습니다."),
    HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND, "보유하지 않은 종목입니다."),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트를 찾을 수 없습니다."),

    // 409
    DUPLICATE_WATCHLIST(HttpStatus.CONFLICT, "이미 등록된 관심종목입니다."),
    EVENT_ALREADY_APPLIED(HttpStatus.CONFLICT, "이미 적용된 이벤트입니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
