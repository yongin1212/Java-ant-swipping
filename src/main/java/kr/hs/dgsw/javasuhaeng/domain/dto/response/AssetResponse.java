package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record AssetResponse(
        BigDecimal initialCash,
        BigDecimal cash,
        BigDecimal stockEvaluation,
        BigDecimal totalAsset,
        BigDecimal profit,
        BigDecimal returnRate
) {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    // 총자산 = 현금 + 주식 평가금액, 손익 = 총자산 - 초기 투자금, 수익률(%) = 손익 / 초기 투자금 * 100
    public static AssetResponse of(Account account, BigDecimal stockEvaluation) {
        BigDecimal initialCash = account.getInitialCash();
        BigDecimal cash = account.getCash();
        BigDecimal totalAsset = cash.add(stockEvaluation);
        BigDecimal profit = totalAsset.subtract(initialCash);
        BigDecimal returnRate = initialCash.signum() == 0
                ? BigDecimal.ZERO.setScale(2)
                : profit.multiply(HUNDRED).divide(initialCash, 2, RoundingMode.HALF_UP);
        return new AssetResponse(initialCash, cash, stockEvaluation, totalAsset, profit, returnRate);
    }
}
