//용인
package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.dto.PriceHistoryDto;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.StockResponse;

import java.util.List;

public interface StockService {
    List<StockResponse> getStocks(String name, Sector sector);
    StockResponse getStock(Long id);
    List<PriceHistoryDto> getPriceHistories(Long id);
}
