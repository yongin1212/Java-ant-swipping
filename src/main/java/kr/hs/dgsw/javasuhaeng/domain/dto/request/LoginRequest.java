package kr.hs.dgsw.javasuhaeng.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max = 50) String loginId,
        @NotBlank @Size(max = 72) String password
) {
}
