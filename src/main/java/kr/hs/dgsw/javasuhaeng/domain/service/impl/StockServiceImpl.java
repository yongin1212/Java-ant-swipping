package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.StockResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.StockService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<StockResponse> getStocks(String name, Sector sector) {
        if (name != null) {
            name = name.trim();
        }
        boolean hasName = name != null && !name.isEmpty();
        List<Stock> stocks;

        if (hasName && sector != null) {
            stocks = stockRepository.findByNameContainingIgnoreCaseAndSectorOrderByIdAsc(name, sector);
        } else if (hasName) {
            stocks = stockRepository.findByNameContainingIgnoreCaseOrderByIdAsc(name);
        } else if (sector != null) {
            stocks = stockRepository.findBySectorOrderByIdAsc(sector);
        } else {
            stocks = stockRepository.findAllByOrderByIdAsc();
        }

        List<StockResponse> responses = new ArrayList<>();
        for (Stock stock : stocks) {
            responses.add(StockResponse.from(stock));
        }
        return responses;
    }

    @Override
    public StockResponse getStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));
        return StockResponse.from(stock);
    }
}
