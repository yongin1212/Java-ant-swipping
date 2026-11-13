package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.WatchlistItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WatchlistResponse(
        Long id,
        Long stockId,
        String stockName,
        BigDecimal targetPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static WatchlistResponse from(WatchlistItem item) {
        return new WatchlistResponse(item.getId(), item.getStock().getId(), item.getStock().getName(),
                item.getTargetPrice(), item.getCreatedAt(), item.getUpdatedAt());
    }
}
