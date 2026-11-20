package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
class InvestmentMemoRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private InvestmentMemoRepository memoRepository;

    private Long memberId;
    private Long stockId;

    @BeforeEach
    void setUp() {
        Member owner = memberRepository.findByLoginId("user1").orElseThrow();
        Member other = memberRepository.findByLoginId("user2").orElseThrow();
        Stock first = stockRepository.findById(1L).orElseThrow();
        Stock second = stockRepository.findById(2L).orElseThrow();

        memoRepository.saveAll(List.of(
                new InvestmentMemo(owner, first, "실적 발표", "다음 분기 확인"),
                new InvestmentMemo(owner, second, "연간 실적", "매출 증가"),
                new InvestmentMemo(owner, first, "가격 변동", "실적은 다음 주 발표"),
                new InvestmentMemo(other, first, "다른 회원의 실적", "비공개")));
        memberId = owner.getId();
        stockId = first.getId();
    }

    @Test
    @DisplayName("조건이 없거나 검색어가 공백이면 내 메모 전체를 조회한다")
    void listsOwnMemos() {
        assertThat(memoRepository.search(memberId, null, null))
                .extracting(InvestmentMemo::getTitle)
                .containsExactlyInAnyOrder("실적 발표", "연간 실적", "가격 변동");
        assertThat(memoRepository.search(memberId, "  ", null))
                .extracting(InvestmentMemo::getTitle)
                .containsExactlyInAnyOrder("실적 발표", "연간 실적", "가격 변동");
    }

    @Test
    @DisplayName("제목 검색어와 종목을 각각 또는 함께 적용한다")
    void filtersOwnMemos() {
        assertThat(memoRepository.search(memberId, "실적", null))
                .extracting(InvestmentMemo::getTitle)
                .containsExactlyInAnyOrder("실적 발표", "연간 실적");
        assertThat(memoRepository.search(memberId, null, stockId))
                .extracting(InvestmentMemo::getTitle)
                .containsExactlyInAnyOrder("실적 발표", "가격 변동");
        assertThat(memoRepository.search(memberId, "실적", stockId))
                .extracting(InvestmentMemo::getTitle)
                .containsExactly("실적 발표");
        assertThat(memoRepository.search(memberId, "배당", stockId)).isEmpty();
    }
}
