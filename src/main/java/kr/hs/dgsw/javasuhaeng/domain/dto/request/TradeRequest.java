package kr.hs.dgsw.javasuhaeng.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TradeRequest(
        @NotNull @Positive Long stockId,
        @NotNull @Min(1) @Max(1_000_000) Integer quantity
) {
}
