package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.dto.response.AccountResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.AssetResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.HoldingResponse;
import kr.hs.dgsw.javasuhaeng.domain.service.AccountService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ApiResponse<AccountResponse> getAccount(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(accountService.getAccount(loginUser.memberId()));
    }

    @GetMapping("/holdings")
    public ApiResponse<List<HoldingResponse>> getHoldings(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(accountService.getHoldings(loginUser.memberId()));
    }

    @GetMapping("/assets")
    public ApiResponse<AssetResponse> getAssets(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(accountService.getAssets(loginUser.memberId()));
    }
}
