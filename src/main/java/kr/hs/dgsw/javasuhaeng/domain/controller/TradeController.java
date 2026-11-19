package kr.hs.dgsw.javasuhaeng.domain.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TradeResponse;
import kr.hs.dgsw.javasuhaeng.domain.service.TradeService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<TradeResponse>> buy(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok(tradeService.buy(loginUser.memberId(), request)));
    }
}
