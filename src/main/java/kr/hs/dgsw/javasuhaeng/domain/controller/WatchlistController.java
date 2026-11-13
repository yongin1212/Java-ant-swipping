package kr.hs.dgsw.javasuhaeng.domain.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.WatchlistCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.WatchlistUpdateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.WatchlistResponse;
import kr.hs.dgsw.javasuhaeng.domain.service.WatchlistService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<WatchlistResponse>> create(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody WatchlistCreateRequest request) {
        WatchlistResponse response = watchlistService.create(loginUser.memberId(), request);
        return ResponseEntity.status(201).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WatchlistResponse>>> findAll(
            @AuthenticationPrincipal LoginUser loginUser) {
        List<WatchlistResponse> response = watchlistService.findAll(loginUser.memberId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WatchlistResponse>> update(
            @AuthenticationPrincipal LoginUser loginUser, @PathVariable Long id,
            @Valid @RequestBody WatchlistUpdateRequest request) {
        WatchlistResponse response = watchlistService.update(loginUser.memberId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Long id) {
        watchlistService.delete(loginUser.memberId(), id);
        return ResponseEntity.noContent().build();
    }
}
