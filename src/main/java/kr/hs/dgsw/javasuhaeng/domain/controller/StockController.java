package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.StockResponse;
import kr.hs.dgsw.javasuhaeng.domain.service.StockService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ApiResponse<List<StockResponse>> getStocks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Sector sector) {
        return ApiResponse.ok(stockService.getStocks(name, sector));
    }

    @GetMapping("/{id}")
    public ApiResponse<StockResponse> getStock(@PathVariable Long id) {
        return ApiResponse.ok(stockService.getStock(id));
    }
}
