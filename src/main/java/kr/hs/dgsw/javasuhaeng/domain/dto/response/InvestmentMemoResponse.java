package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;

import java.time.LocalDateTime;

public record InvestmentMemoResponse(
        Long id,
        Long stockId,
        String stockName,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static InvestmentMemoResponse from(InvestmentMemo memo) {
        return new InvestmentMemoResponse(memo.getId(), memo.getStock().getId(), memo.getStock().getName(),
                memo.getTitle(), memo.getContent(), memo.getCreatedAt(), memo.getUpdatedAt());
    }
}
