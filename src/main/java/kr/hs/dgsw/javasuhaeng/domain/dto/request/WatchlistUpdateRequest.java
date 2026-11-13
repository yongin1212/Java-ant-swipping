package kr.hs.dgsw.javasuhaeng.domain.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WatchlistUpdateRequest(
        @Positive(message = "목표 가격은 양수여야 합니다.")
        @Digits(integer = 19, fraction = 0, message = "목표 가격은 최대 19자리의 원 단위 정수여야 합니다.")
        BigDecimal targetPrice
) {
}
