//용인
package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.MarketEvent;
import kr.hs.dgsw.javasuhaeng.domain.domain.PriceHistory;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.dto.MarketEventDto;
import kr.hs.dgsw.javasuhaeng.domain.repository.MarketEventRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.PriceHistoryRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.MarketEventService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarketEventServiceImpl implements MarketEventService {

    private final MarketEventRepository eventRepository;
    private final StockRepository stockRepository;
    private final PriceHistoryRepository historyRepository;

    @Override
    public List<MarketEventDto> findAll() {
        List<MarketEventDto> result = new ArrayList<>();
        for (MarketEvent event : eventRepository.findAllByOrderByIdDesc()) {
            result.add(MarketEventDto.from(event));
        }
        return result;
    }

    @Override
    public MarketEventDto findById(Long id) {
        MarketEvent event = findEvent(id);
        return MarketEventDto.from(event);
    }

    @Override
    public MarketEventDto create(MarketEventDto dto) {
        Stock stock = validate(dto);
        MarketEvent event = new MarketEvent(dto.getTitle(), dto.getDescription(), dto.getTargetType(),
                stock, dto.getSector(), dto.getChangeRate());
        eventRepository.save(event);
        return MarketEventDto.from(event);
    }

    @Override
    public MarketEventDto update(Long id, MarketEventDto dto) {
        MarketEvent event = findEventForUpdate(id);
        if ("APPLIED".equals(event.getStatus())) {
            throw new BusinessException(ErrorCode.EVENT_ALREADY_APPLIED);
        }
        Stock stock = validate(dto);
        event.update(dto.getTitle(), dto.getDescription(), dto.getTargetType(),
                stock, dto.getSector(), dto.getChangeRate());
        eventRepository.save(event);
        return MarketEventDto.from(event);
    }

    @Override
    public void delete(Long id) {
        MarketEvent event = findEventForUpdate(id);
        if ("APPLIED".equals(event.getStatus())) {
            throw new BusinessException(ErrorCode.EVENT_ALREADY_APPLIED);
        }
        eventRepository.delete(event);
    }

    @Override
    public MarketEventDto apply(Long id) {
        MarketEvent event = findEventForUpdate(id);
        if ("APPLIED".equals(event.getStatus())) {
            throw new BusinessException(ErrorCode.EVENT_ALREADY_APPLIED);
        }

        List<Stock> stocks = new ArrayList<>();
        if ("STOCK".equals(event.getTargetType())) {
            Stock stock = stockRepository.findByIdForUpdate(event.getTargetStock().getId()).orElse(null);
            if (stock == null) {
                throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
            }
            stocks.add(stock);
        } else if ("SECTOR".equals(event.getTargetType())) {
            stocks = stockRepository.findBySectorForUpdate(event.getTargetSector());
        } else {
            stocks = stockRepository.findAllForUpdate();
        }

        for (Stock stock : stocks) {
            BigDecimal beforePrice = stock.getCurrentPrice();
            BigDecimal rate = event.getChangeRate().divide(new BigDecimal("100"));
            BigDecimal afterPrice = beforePrice.multiply(BigDecimal.ONE.add(rate));
            afterPrice = afterPrice.setScale(0, RoundingMode.HALF_UP);
            if (afterPrice.compareTo(BigDecimal.ONE) < 0) {
                afterPrice = BigDecimal.ONE;
            }
            stock.changePrice(afterPrice);
            historyRepository.save(new PriceHistory(stock, event, beforePrice, afterPrice));
        }
        event.markApplied();
        return MarketEventDto.from(event);
    }

    private MarketEvent findEventForUpdate(Long id) {
        MarketEvent event = eventRepository.findByIdForUpdate(id).orElse(null);
        if (event == null) {
            throw new BusinessException(ErrorCode.EVENT_NOT_FOUND);
        }
        return event;
    }

    private MarketEvent findEvent(Long id) {
        MarketEvent event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            throw new BusinessException(ErrorCode.EVENT_NOT_FOUND);
        }
        return event;
    }

    private Stock validate(MarketEventDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank() || dto.getTitle().length() > 100) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank() || dto.getDescription().length() > 1000) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (dto.getChangeRate() == null || dto.getChangeRate().compareTo(BigDecimal.ZERO) == 0
                || dto.getChangeRate().compareTo(new BigDecimal("-50")) < 0
                || dto.getChangeRate().compareTo(new BigDecimal("50")) > 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if ("STOCK".equals(dto.getTargetType())) {
            if (dto.getStockId() == null || dto.getSector() != null) {
                throw new BusinessException(ErrorCode.INVALID_EVENT_TARGET);
            }
            Stock stock = stockRepository.findById(dto.getStockId()).orElse(null);
            if (stock == null) {
                throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
            }
            return stock;
        }
        if ("SECTOR".equals(dto.getTargetType())) {
            if (dto.getSector() == null || dto.getStockId() != null) {
                throw new BusinessException(ErrorCode.INVALID_EVENT_TARGET);
            }
        } else if ("ALL".equals(dto.getTargetType())) {
            if (dto.getStockId() != null || dto.getSector() != null) {
                throw new BusinessException(ErrorCode.INVALID_EVENT_TARGET);
            }
        } else {
            throw new BusinessException(ErrorCode.INVALID_EVENT_TARGET);
        }
        return null;
    }
}
