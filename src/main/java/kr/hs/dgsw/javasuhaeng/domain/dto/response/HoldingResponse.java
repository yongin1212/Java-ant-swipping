package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.Holding;

import java.math.BigDecimal;

public record HoldingResponse(
        Long stockId,
        String stockCode,
        String stockName,
        long quantity,
        BigDecimal currentPrice,
        BigDecimal evaluationAmount
) {
    public static HoldingResponse from(Holding holding) {
        return new HoldingResponse(holding.getStock().getId(), holding.getStock().getCode(),
                holding.getStock().getName(), holding.getQuantity(), holding.getStock().getCurrentPrice(),
                holding.getEvaluationAmount());
    }
}
