package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.TradeTestSupport;
import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Trade;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.repository.TradeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// 테스트 트랜잭션 없이 실제 커밋/롤백을 확인한다.
@SpringBootTest
class TradeRollbackTest extends TradeTestSupport {

    @Autowired
    private TradeService tradeService;

    // 거래 기록 저장 단계에서 실패시키기 위한 가짜 Repository
    @MockitoBean
    private TradeRepository tradeRepository;

    private Account account;

    @AfterEach
    void tearDown() {
        holdingRepository.deleteAll(holdingRepository.findAllWithStockByAccountId(account.getId()));
        accountRepository.deleteById(account.getId());
        memberRepository.deleteById(account.getMember().getId());
    }

    @Test
    @DisplayName("거래 기록 저장이 실패하면 이미 바꾼 현금과 보유 수량도 함께 롤백된다")
    void buy_rollbackWhenTradeSaveFails() {
        account = createAccount(new BigDecimal("1000000"));
        given(tradeRepository.save(any(Trade.class))).willThrow(new IllegalStateException("저장 실패"));

        assertThatThrownBy(() -> tradeService.buy(account.getMember().getId(), new TradeRequest(1L, 10)))
                .isInstanceOf(IllegalStateException.class);

        Account reloaded = accountRepository.findById(account.getId()).orElseThrow();
        assertThat(reloaded.getCash()).isEqualByComparingTo("1000000");
        assertThat(holdingRepository.findByAccountIdAndStockId(account.getId(), 1L)).isEmpty();
    }
}
