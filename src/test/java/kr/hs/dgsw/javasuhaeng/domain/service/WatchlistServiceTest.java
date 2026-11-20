package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.request.WatchlistCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.WatchlistResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class WatchlistServiceTest {

    @Autowired
    private WatchlistService watchlistService;
    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.findByLoginId("user1").orElseThrow().getId();
    }

    @Test
    @DisplayName("관심종목 등록 성공: 한빛전자와 목표 가격을 저장한다")
    void create_success() {
        WatchlistResponse response = watchlistService.create(
                memberId, new WatchlistCreateRequest(1L, new BigDecimal("12000")));

        assertThat(response.id()).isNotNull();
        assertThat(response.stockId()).isEqualTo(1L);
        assertThat(response.stockName()).isEqualTo("한빛전자");
        assertThat(response.targetPrice()).isEqualByComparingTo("12000");
        assertThat(watchlistService.findAll(memberId)).hasSize(1);
    }

    @Test
    @DisplayName("관심종목 중복 등록 실패: 같은 종목은 한 번만 등록된다")
    void create_duplicate() {
        WatchlistCreateRequest request = new WatchlistCreateRequest(1L, new BigDecimal("12000"));
        watchlistService.create(memberId, request);

        BusinessException error = assertThrows(BusinessException.class, () -> watchlistService.create(memberId, request));
        assertThat(error.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_WATCHLIST);
        assertThat(watchlistService.findAll(memberId)).hasSize(1);
    }
}
