package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;

import java.math.BigDecimal;

public record StockResponse(Long id, String code, String name, Sector sector, BigDecimal currentPrice) {

    public static StockResponse from(Stock stock) {
        return new StockResponse(stock.getId(), stock.getCode(), stock.getName(), stock.getSector(), stock.getCurrentPrice());
    }
}
