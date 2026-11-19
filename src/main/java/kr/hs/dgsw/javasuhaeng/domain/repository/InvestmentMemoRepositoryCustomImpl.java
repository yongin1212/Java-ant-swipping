package kr.hs.dgsw.javasuhaeng.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

import static kr.hs.dgsw.javasuhaeng.domain.domain.QInvestmentMemo.investmentMemo;

@RequiredArgsConstructor
public class InvestmentMemoRepositoryCustomImpl implements InvestmentMemoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<InvestmentMemo> search(Long memberId, String keyword, Long stockId) {
        BooleanBuilder conditions = new BooleanBuilder(investmentMemo.member.id.eq(memberId));
        if (StringUtils.hasText(keyword)) {
            conditions.and(investmentMemo.title.containsIgnoreCase(keyword.strip()));
        }
        if (stockId != null) {
            conditions.and(investmentMemo.stock.id.eq(stockId));
        }

        return queryFactory.selectFrom(investmentMemo)
                .join(investmentMemo.stock).fetchJoin()
                .where(conditions)
                .fetch();
    }
}
