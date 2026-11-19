package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.TradeTestSupport;
import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Holding;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.domain.TradeType;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TradeResponse;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
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
class TradeServiceTest extends TradeTestSupport {

    @Autowired
    private TradeService tradeService;

    private Account account;
    private Stock stock;
    private Long memberId;

    @BeforeEach
    void setUp() {
        account = createAccount(new BigDecimal("1000000"));
        memberId = account.getMember().getId();
        stock = stock(1L);
    }

    @Test
    @DisplayName("매수 성공: 현재가로 체결되고 잔액 감소, 보유 수량 증가, 거래 기록 저장")
    void buy_success() {
        BigDecimal price = stock.getCurrentPrice();

        TradeResponse response = tradeService.buy(memberId, new TradeRequest(stock.getId(), 10));

        BigDecimal total = price.multiply(BigDecimal.TEN);
        assertThat(response.type()).isEqualTo(TradeType.BUY);
        assertThat(response.price()).isEqualByComparingTo(price);
        assertThat(response.totalAmount()).isEqualByComparingTo(total);
        assertThat(account.getCash()).isEqualByComparingTo(new BigDecimal("1000000").subtract(total));
        Holding holding = holdingRepository.findByAccountIdAndStockId(account.getId(), stock.getId()).orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(10);
        assertThat(tradeRepository.findAllByAccountIdOrderByTradedAtDescIdDesc(account.getId())).hasSize(1);
    }

    @Test
    @DisplayName("같은 종목을 두 번 매수하면 보유 수량이 합쳐진다")
    void buy_twice_accumulates() {
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 3));
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 4));

        Holding holding = holdingRepository.findByAccountIdAndStockId(account.getId(), stock.getId()).orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("잔액 부족 매수 실패: INSUFFICIENT_CASH, 잔액과 보유 수량 변화 없음")
    void buy_insufficientCash() {
        int tooMany = new BigDecimal("1000000").divide(stock.getCurrentPrice()).intValue() + 1;

        assertThatThrownBy(() -> tradeService.buy(memberId, new TradeRequest(stock.getId(), tooMany)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INSUFFICIENT_CASH);
        assertThat(account.getCash()).isEqualByComparingTo("1000000");
        assertThat(holdingRepository.findByAccountIdAndStockId(account.getId(), stock.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 종목 매수 실패: STOCK_NOT_FOUND")
    void buy_stockNotFound() {
        assertThatThrownBy(() -> tradeService.buy(memberId, new TradeRequest(9999L, 1)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.STOCK_NOT_FOUND);
    }

    @Test
    @DisplayName("매도 성공: 잔액 증가, 보유 수량 감소")
    void sell_success() {
        BigDecimal price = stock.getCurrentPrice();
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 10));

        TradeResponse response = tradeService.sell(memberId, new TradeRequest(stock.getId(), 4));

        assertThat(response.type()).isEqualTo(TradeType.SELL);
        assertThat(account.getCash())
                .isEqualByComparingTo(new BigDecimal("1000000").subtract(price.multiply(BigDecimal.valueOf(6))));
        Holding holding = holdingRepository.findByAccountIdAndStockId(account.getId(), stock.getId()).orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("전량 매도하면 보유 주식에서 사라진다")
    void sell_all_removesHolding() {
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 5));

        tradeService.sell(memberId, new TradeRequest(stock.getId(), 5));

        assertThat(holdingRepository.findByAccountIdAndStockId(account.getId(), stock.getId())).isEmpty();
        assertThat(account.getCash()).isEqualByComparingTo("1000000");
    }

    @Test
    @DisplayName("보유 수량 부족 매도 실패: INSUFFICIENT_QUANTITY, 보유 수량 변화 없음")
    void sell_insufficientQuantity() {
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 3));

        assertThatThrownBy(() -> tradeService.sell(memberId, new TradeRequest(stock.getId(), 4)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INSUFFICIENT_QUANTITY);
        Holding holding = holdingRepository.findByAccountIdAndStockId(account.getId(), stock.getId()).orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("보유하지 않은 종목 매도 실패: HOLDING_NOT_FOUND")
    void sell_holdingNotFound() {
        assertThatThrownBy(() -> tradeService.sell(memberId, new TradeRequest(stock.getId(), 1)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.HOLDING_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 내역은 최신순으로 조회된다")
    void getTrades_latestFirst() {
        tradeService.buy(memberId, new TradeRequest(stock.getId(), 5));
        tradeService.sell(memberId, new TradeRequest(stock.getId(), 2));

        List<TradeResponse> trades = tradeService.getTrades(memberId);

        assertThat(trades).extracting(TradeResponse::type).containsExactly(TradeType.SELL, TradeType.BUY);
        assertThat(trades.get(0).stockName()).isEqualTo(stock.getName());
    }
}
