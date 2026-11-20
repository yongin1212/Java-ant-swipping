package kr.hs.dgsw.javasuhaeng.domain.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

public record WatchlistCreateRequest(
        @JsonDeserialize(using = StockIdDeserializer.class)
        @NotNull(message = "종목 ID는 필수입니다.")
        @Positive(message = "종목 ID는 양수여야 합니다.") Long stockId,
        @Positive(message = "목표 가격은 양수여야 합니다.")
        @Digits(integer = 19, fraction = 0, message = "목표 가격은 최대 19자리의 원 단위 정수여야 합니다.")
        BigDecimal targetPrice
) {
}
