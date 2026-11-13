package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.domain.WatchlistItem;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.WatchlistCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.WatchlistUpdateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.WatchlistResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.WatchlistRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.WatchlistService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;

    @Override
    public WatchlistResponse create(Long memberId, WatchlistCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElse(null);
        if (member == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Stock stock = stockRepository.findById(request.stockId())
                .orElse(null);
        if (stock == null) {
            throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        }

        if (watchlistRepository.existsByMemberIdAndStockId(memberId, request.stockId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_WATCHLIST);
        }

        WatchlistItem item = new WatchlistItem(member, stock, request.targetPrice());
        try {
            return WatchlistResponse.from(watchlistRepository.saveAndFlush(item));
        } catch (DataIntegrityViolationException exception) {
            if (exception.getCause() instanceof ConstraintViolationException violation
                    && "23505".equals(violation.getSQLState())) {
                throw new BusinessException(ErrorCode.DUPLICATE_WATCHLIST);
            }
            throw exception;
        }
    }

    @Override
    public List<WatchlistResponse> findAll(Long memberId) {
        List<WatchlistItem> items = watchlistRepository.findAllByMemberId(memberId);
        return items.stream()
                .map(item -> WatchlistResponse.from(item))
                .toList();
    }

    @Override
    public WatchlistResponse update(Long memberId, Long id, WatchlistUpdateRequest request) {
        WatchlistItem item = findOwnedItem(memberId, id);
        item.updateTargetPrice(request.targetPrice());
        watchlistRepository.saveAndFlush(item);
        return WatchlistResponse.from(item);
    }

    @Override
    public void delete(Long memberId, Long id) {
        WatchlistItem item = findOwnedItem(memberId, id);
        watchlistRepository.delete(item);
    }

    private WatchlistItem findOwnedItem(Long memberId, Long id) {
        WatchlistItem item = watchlistRepository.findById(id)
                .orElse(null);
        if (item == null) {
            throw new BusinessException(ErrorCode.WATCHLIST_NOT_FOUND);
        }
        if (!item.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return item;
    }
}
