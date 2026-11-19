package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.TradeTestSupport;
import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.AssetResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.HoldingResponse;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AccountServiceTest extends TradeTestSupport {

    @Autowired
    private AccountService accountService;
    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("총자산, 손익, 수익률 계산: 시세 변경 후 평가금액이 반영된다")
    void getAssets_afterPriceChange() {
        Account account = createAccount(new BigDecimal("1000000"));
        Long memberId = account.getMember().getId();
        Stock stock = stock(1L);
        stock.changePrice(new BigDecimal("10000"));
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 30));   // 현금 700,000
        stock.changePrice(new BigDecimal("11000"));                         // 평가금액 330,000

        AssetResponse assets = accountService.getAssets(memberId);

        assertThat(assets.cash()).isEqualByComparingTo("700000");
        assertThat(assets.stockEvaluation()).isEqualByComparingTo("330000");
        assertThat(assets.totalAsset()).isEqualByComparingTo("1030000");
        assertThat(assets.profit()).isEqualByComparingTo("30000");
        assertThat(assets.returnRate()).isEqualByComparingTo("3.00");
    }

    @Test
    @DisplayName("수익률은 소수점 둘째 자리에서 반올림하고 손실은 음수로 나온다")
    void getAssets_lossRounding() {
        Account account = createAccount(new BigDecimal("3000000"));
        Long memberId = account.getMember().getId();
        Stock stock = stock(1L);
        stock.changePrice(new BigDecimal("10000"));
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 1));
        stock.changePrice(new BigDecimal("9999"));                          // 손익 -1

        AssetResponse assets = accountService.getAssets(memberId);

        assertThat(assets.profit()).isEqualByComparingTo("-1");
        assertThat(assets.returnRate()).isEqualByComparingTo("0.00");       // -0.0000333% -> 0.00
    }

    @Test
    @DisplayName("보유 주식 조회: 종목별 수량, 현재가, 평가금액")
    void getHoldings() {
        Account account = createAccount(new BigDecimal("1000000"));
        Long memberId = account.getMember().getId();
        Stock stock1 = stock(1L);
        Stock stock3 = stock(3L);
        tradeService.buy(memberId, new TradeRequest(stock1.getId(), 2));
        tradeService.buy(memberId, new TradeRequest(stock3.getId(), 3));

        List<HoldingResponse> holdings = accountService.getHoldings(memberId);

        assertThat(holdings).extracting(HoldingResponse::stockId).containsExactly(1L, 3L);
        HoldingResponse second = holdings.get(1);
        assertThat(second.quantity()).isEqualTo(3);
        assertThat(second.evaluationAmount())
                .isEqualByComparingTo(stock3.getCurrentPrice().multiply(BigDecimal.valueOf(3)));
    }

    @Test
    @DisplayName("계좌가 없는 회원 조회 실패: ACCOUNT_NOT_FOUND")
    void getAccount_notFound() {
        assertThatThrownBy(() -> accountService.getAccount(99999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
