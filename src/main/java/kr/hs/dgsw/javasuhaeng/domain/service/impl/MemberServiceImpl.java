package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.LoginRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.MemberResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TokenResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.MemberService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import kr.hs.dgsw.javasuhaeng.global.security.JwtTokenProvider;
import kr.hs.dgsw.javasuhaeng.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72
                || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return createToken(member);
    }

    @Override
    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
        return MemberResponse.from(member);
    }

    @Override
    public LoginUser authenticate(Jwt jwt) {
        Member member = memberRepository.findById(getMemberId(jwt))
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
        validateToken(member, jwt);
        return new LoginUser(member.getId(), member.getRole());
    }

    private TokenResponse createToken(Member member) {
        String tokenId = UUID.randomUUID().toString();
        String accessToken = tokenProvider.createAccessToken(member.getId(), tokenId);
        member.updateToken(tokenId);
        return new TokenResponse("Bearer", accessToken, JwtTokenProvider.ACCESS_TOKEN_SECONDS);
    }

    private void validateToken(Member member, Jwt jwt) {
        if (member.getTokenId() == null || !member.getTokenId().equals(jwt.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (jwt.getExpiresAt() == null || !jwt.getExpiresAt().isAfter(Instant.now())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    private Long getMemberId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }
}
