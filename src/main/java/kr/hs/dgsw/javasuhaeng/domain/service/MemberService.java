package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.request.LoginRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.MemberResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TokenResponse;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import org.springframework.security.oauth2.jwt.Jwt;

public interface MemberService {
    TokenResponse login(LoginRequest request);
    MemberResponse getMember(Long memberId);
    LoginUser authenticate(Jwt jwt);
}
