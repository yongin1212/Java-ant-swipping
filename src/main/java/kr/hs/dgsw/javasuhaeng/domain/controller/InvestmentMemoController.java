package kr.hs.dgsw.javasuhaeng.domain.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoUpdateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.InvestmentMemoResponse;
import kr.hs.dgsw.javasuhaeng.domain.service.InvestmentMemoService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class InvestmentMemoController {

    private final InvestmentMemoService investmentMemoService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvestmentMemoResponse>> create(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody InvestmentMemoCreateRequest request) {
        InvestmentMemoResponse response = investmentMemoService.create(loginUser.memberId(), request);
        return ResponseEntity.status(201).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestmentMemoResponse>>> findAll(
            @AuthenticationPrincipal LoginUser loginUser) {
        List<InvestmentMemoResponse> response = investmentMemoService.findAll(loginUser.memberId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvestmentMemoResponse>> findById(
            @AuthenticationPrincipal LoginUser loginUser, @PathVariable Long id) {
        InvestmentMemoResponse response = investmentMemoService.findById(loginUser.memberId(), id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvestmentMemoResponse>> update(
            @AuthenticationPrincipal LoginUser loginUser, @PathVariable Long id,
            @Valid @RequestBody InvestmentMemoUpdateRequest request) {
        InvestmentMemoResponse response = investmentMemoService.update(loginUser.memberId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Long id) {
        investmentMemoService.delete(loginUser.memberId(), id);
        return ResponseEntity.noContent().build();
    }
}
