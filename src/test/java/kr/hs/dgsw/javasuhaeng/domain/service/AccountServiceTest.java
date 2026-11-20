package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Role;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.AssetResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.AccountRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("총자산 조회: 주가가 오르면 수익률이 올라간다")
    void getAssets_priceUp() {
        Member member = memberRepository.save(new Member("tester", "pw", "테스터", Role.USER));
        accountRepository.save(new Account(member, new BigDecimal("1000000")));

        tradeService.buy(member.getId(), new TradeRequest(1L, 30));   // 10,000원 x 30주 매수, 현금 700,000
        Stock stock = stockRepository.findById(1L).orElseThrow();
        stock.changePrice(new BigDecimal("11000"));                    // 주가 10% 상승

        AssetResponse assets = accountService.getAssets(member.getId());

        assertThat(assets.stockEvaluation()).isEqualByComparingTo("330000");  // 11,000원 x 30주
        assertThat(assets.totalAsset()).isEqualByComparingTo("1030000");      // 700,000 + 330,000
        assertThat(assets.profit()).isEqualByComparingTo("30000");
        assertThat(assets.returnRate()).isEqualByComparingTo("3.00");         // 30,000 / 1,000,000 x 100
    }

    @Test
    @DisplayName("계좌 조회 실패: 계좌가 없으면 예외가 발생한다")
    void getAccount_fail_noAccount() {
        assertThatThrownBy(() -> accountService.getAccount(99999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("계좌를 찾을 수 없습니다.");
    }
}
