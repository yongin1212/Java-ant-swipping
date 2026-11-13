package kr.hs.dgsw.javasuhaeng.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record InvestmentMemoCreateRequest(
        @NotNull(message = "종목 ID는 필수입니다.")
        @Positive(message = "종목 ID는 양수여야 합니다.") Long stockId,
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 50, message = "제목은 50자 이하여야 합니다.") String title,
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 1000, message = "내용은 1,000자 이하여야 합니다.") String content
) {
}
