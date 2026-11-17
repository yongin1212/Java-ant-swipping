//용인
package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.dto.response.MemberResponse;
import kr.hs.dgsw.javasuhaeng.domain.service.MemberService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ApiResponse<MemberResponse> me(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(memberService.getMember(loginUser.memberId()));
    }
}
