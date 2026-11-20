package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoUpdateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.InvestmentMemoResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class InvestmentMemoServiceTest {

    @Autowired
    private InvestmentMemoService investmentMemoService;
    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;
    private Long memoId;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.findByLoginId("user1").orElseThrow().getId();
        memoId = investmentMemoService.create(memberId,
                new InvestmentMemoCreateRequest(1L, "기존 제목", "기존 내용")).id();
    }

    @Test
    @DisplayName("본인 메모 수정 성공: 제목과 내용을 바꾼다")
    void update_success() {
        InvestmentMemoResponse response = investmentMemoService.update(memberId, memoId,
                new InvestmentMemoUpdateRequest("실적 발표", "실적 확인 후 매수 예정"));

        assertThat(response.id()).isEqualTo(memoId);
        assertThat(response.stockId()).isEqualTo(1L);
        InvestmentMemoResponse saved = investmentMemoService.findById(memberId, memoId);
        assertThat(saved.title()).isEqualTo("실적 발표");
        assertThat(saved.content()).isEqualTo("실적 확인 후 매수 예정");
    }

    @Test
    @DisplayName("다른 회원의 메모 수정 실패: 원래 제목과 내용을 유지한다")
    void update_otherMember() {
        Long otherMemberId = memberRepository.findByLoginId("user2").orElseThrow().getId();
        InvestmentMemoUpdateRequest request = new InvestmentMemoUpdateRequest("변경 제목", "변경 내용");
        BusinessException error = assertThrows(BusinessException.class,
                () -> investmentMemoService.update(otherMemberId, memoId, request));
        assertThat(error.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);

        InvestmentMemoResponse saved = investmentMemoService.findById(memberId, memoId);
        assertThat(saved.title()).isEqualTo("기존 제목");
        assertThat(saved.content()).isEqualTo("기존 내용");
    }
}
