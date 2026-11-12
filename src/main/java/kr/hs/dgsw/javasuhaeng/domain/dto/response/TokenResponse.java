package kr.hs.dgsw.javasuhaeng.domain.dto.response;

public record TokenResponse(String tokenType, String accessToken, long expiresIn) {
}
