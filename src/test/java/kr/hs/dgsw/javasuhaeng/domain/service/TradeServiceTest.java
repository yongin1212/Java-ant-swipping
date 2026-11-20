package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Role;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TradeResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.AccountRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 테스트가 끝나면 @Transactional 덕분에 DB 변경이 모두 되돌아간다.
@SpringBootTest
@Transactional
class TradeServiceTest {

    @Autowired
    private TradeService tradeService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AccountRepository accountRepository;

    private Long memberId;
    private Account account;

    // 테스트마다 현금 100만 원짜리 계좌를 만든다. 1번 종목(한빛전자)의 가격은 10,000원이다.
    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(new Member("tester", "pw", "테스터", Role.USER));
        account = accountRepository.save(new Account(member, new BigDecimal("1000000")));
        memberId = member.getId();
    }

    @Test
    @DisplayName("매수 성공: 현금이 줄어든다")
    void buy_success() {
        TradeResponse response = tradeService.buy(memberId, new TradeRequest(1L, 10));

        assertThat(response.quantity()).isEqualTo(10);
        assertThat(response.totalAmount()).isEqualByComparingTo("100000");   // 10,000원 x 10주
        assertThat(account.getCash()).isEqualByComparingTo("900000");        // 1,000,000 - 100,000
    }

    @Test
    @DisplayName("매수 실패: 현금이 부족하면 예외가 발생한다")
    void buy_fail_notEnoughCash() {
        // 10,000원 x 101주 = 1,010,000원 > 현금 1,000,000원
        assertThatThrownBy(() -> tradeService.buy(memberId, new TradeRequest(1L, 101)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("잔액이 부족합니다.");
    }

    @Test
    @DisplayName("매도 성공: 현금이 다시 늘어난다")
    void sell_success() {
        tradeService.buy(memberId, new TradeRequest(1L, 10));    // 현금 900,000

        tradeService.sell(memberId, new TradeRequest(1L, 4));    // 4주 x 10,000 = 40,000 입금

        assertThat(account.getCash()).isEqualByComparingTo("940000");
    }

    @Test
    @DisplayName("매도 실패: 가진 수량보다 많이 팔면 예외가 발생한다")
    void sell_fail_notEnoughQuantity() {
        tradeService.buy(memberId, new TradeRequest(1L, 3));

        assertThatThrownBy(() -> tradeService.sell(memberId, new TradeRequest(1L, 5)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("보유 수량이 부족합니다.");
    }
}
