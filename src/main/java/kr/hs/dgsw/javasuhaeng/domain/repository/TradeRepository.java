package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.Trade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    // 최신순, 종목명 표시를 위해 stock을 함께 조회
    @EntityGraph(attributePaths = "stock")
    List<Trade> findAllByAccountIdOrderByTradedAtDescIdDesc(Long accountId);
}
