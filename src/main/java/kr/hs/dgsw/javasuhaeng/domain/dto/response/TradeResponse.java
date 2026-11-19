package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.Trade;
import kr.hs.dgsw.javasuhaeng.domain.domain.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeResponse(
        Long tradeId,
        Long stockId,
        String stockName,
        TradeType type,
        int quantity,
        BigDecimal price,
        BigDecimal totalAmount,
        LocalDateTime tradedAt
) {
    public static TradeResponse from(Trade trade) {
        return new TradeResponse(trade.getId(), trade.getStock().getId(), trade.getStock().getName(),
                trade.getType(), trade.getQuantity(), trade.getPrice(), trade.getTotalAmount(), trade.getTradedAt());
    }
}
