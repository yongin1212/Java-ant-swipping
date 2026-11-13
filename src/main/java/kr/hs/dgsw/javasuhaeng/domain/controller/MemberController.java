package kr.hs.dgsw.javasuhaeng.domain.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.LoginRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.MemberResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TokenResponse;
import kr.hs.dgsw.javasuhaeng.domain.service.MemberService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(memberService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<MemberResponse> me(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(memberService.getMember(loginUser.memberId()));
    }
}
