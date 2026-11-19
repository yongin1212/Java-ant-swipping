package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.TradeTestSupport;
import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

// 비관적 잠금(Stock -> Account -> Holding) 확인용. 테스트 트랜잭션 없이 실제 동시 요청을 보낸다.
@SpringBootTest
class TradeConcurrencyTest extends TradeTestSupport {

    private static final int THREADS = 10;

    @Autowired
    private TradeService tradeService;

    private Account account;

    @AfterEach
    void tearDown() {
        deleteAccount(account);
    }

    @Test
    @DisplayName("동시에 매수해도 잔액이 음수가 되지 않고 살 수 있는 만큼만 체결된다")
    void concurrentBuy_neverOverdraws() throws Exception {
        BigDecimal price = stock(1L).getCurrentPrice();
        account = createAccount(price.multiply(BigDecimal.valueOf(5)));   // 1주씩 5번만 살 수 있는 금액
        Long memberId = account.getMember().getId();

        AtomicInteger success = new AtomicInteger();
        AtomicInteger insufficientCash = new AtomicInteger();
        runConcurrently(() -> {
            try {
                tradeService.buy(memberId, new TradeRequest(1L, 1));
                success.incrementAndGet();
            } catch (BusinessException e) {
                if (e.getErrorCode() == ErrorCode.INSUFFICIENT_CASH) {
                    insufficientCash.incrementAndGet();
                }
            }
        });

        assertThat(success.get()).isEqualTo(5);
        assertThat(insufficientCash.get()).isEqualTo(THREADS - 5);
        assertThat(accountRepository.findById(account.getId()).orElseThrow().getCash()).isEqualByComparingTo("0");
        assertThat(holdingRepository.findByAccountIdAndStockId(account.getId(), 1L).orElseThrow().getQuantity())
                .isEqualTo(5);
        assertThat(tradeRepository.findAllByAccountIdOrderByTradedAtDescIdDesc(account.getId())).hasSize(5);
    }

    @Test
    @DisplayName("동시에 매도해도 보유 수량이 음수가 되지 않는다")
    void concurrentSell_neverOversells() throws Exception {
        BigDecimal price = stock(1L).getCurrentPrice();
        account = createAccount(price.multiply(BigDecimal.valueOf(3)));
        Long memberId = account.getMember().getId();
        tradeService.buy(memberId, new TradeRequest(1L, 3));

        AtomicInteger success = new AtomicInteger();
        runConcurrently(() -> {
            try {
                tradeService.sell(memberId, new TradeRequest(1L, 1));
                success.incrementAndGet();
            } catch (BusinessException ignored) {
                // 보유 수량 부족 또는 전량 매도 후 미보유
            }
        });

        assertThat(success.get()).isEqualTo(3);
        assertThat(holdingRepository.findByAccountIdAndStockId(account.getId(), 1L)).isEmpty();
        assertThat(accountRepository.findById(account.getId()).orElseThrow().getCash())
                .isEqualByComparingTo(price.multiply(BigDecimal.valueOf(3)));
    }

    private void runConcurrently(Runnable task) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            futures.add(executor.submit(() -> {
                start.await();
                task.run();
                return null;
            }));
        }
        start.countDown();
        for (Future<?> future : futures) {
            future.get(30, TimeUnit.SECONDS);
        }
        executor.shutdown();
    }
}
