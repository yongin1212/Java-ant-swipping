package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Holding;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.domain.Trade;
import kr.hs.dgsw.javasuhaeng.domain.domain.TradeType;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TradeResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.AccountRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.HoldingRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.TradeRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.TradeService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// 현금 변경, 보유 수량 변경, 거래 기록 저장은 하나의 트랜잭션. 중간에 실패하면 전체 롤백.
// 잠금 순서는 이벤트 적용과 맞춰 Stock -> Account -> Holding.
@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

    private final StockRepository stockRepository;
    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final TradeRepository tradeRepository;

    @Override
    public TradeResponse buy(Long memberId, TradeRequest request) {
        Stock stock = lockStock(request.stockId());
        Account account = lockAccount(memberId);

        // 체결 가격은 요청값이 아닌 서버의 현재가
        BigDecimal price = stock.getCurrentPrice();
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(request.quantity()));
        account.withdraw(totalAmount);

        Holding holding = holdingRepository.findByAccountIdAndStockIdForUpdate(account.getId(), stock.getId())
                .orElseGet(() -> holdingRepository.save(new Holding(account, stock)));
        holding.increase(request.quantity());

        Trade trade = tradeRepository.save(new Trade(account, stock, TradeType.BUY, request.quantity(), price));
        return TradeResponse.from(trade);
    }

    @Override
    public TradeResponse sell(Long memberId, TradeRequest request) {
        Stock stock = lockStock(request.stockId());
        Account account = lockAccount(memberId);

        Holding holding = holdingRepository.findByAccountIdAndStockIdForUpdate(account.getId(), stock.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOLDING_NOT_FOUND));
        holding.decrease(request.quantity());
        if (holding.isEmpty()) {
            holdingRepository.delete(holding);
        }

        BigDecimal price = stock.getCurrentPrice();
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(request.quantity()));
        account.deposit(totalAmount);

        Trade trade = tradeRepository.save(new Trade(account, stock, TradeType.SELL, request.quantity(), price));
        return TradeResponse.from(trade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeResponse> getTrades(Long memberId) {
        Account account = accountRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        return tradeRepository.findAllByAccountIdOrderByTradedAtDescIdDesc(account.getId()).stream()
                .map(TradeResponse::from)
                .toList();
    }

    private Stock lockStock(Long stockId) {
        return stockRepository.findByIdForUpdate(stockId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
    }

    private Account lockAccount(Long memberId) {
        return accountRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }
}
