package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentMemoRepository extends JpaRepository<InvestmentMemo, Long> {

    @EntityGraph(attributePaths = "stock")
    List<InvestmentMemo> findAllByMemberId(Long memberId);
}
