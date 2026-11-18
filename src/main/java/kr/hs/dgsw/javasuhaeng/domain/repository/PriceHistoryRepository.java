//용인
package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.PriceHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    @EntityGraph(attributePaths = "event")
    List<PriceHistory> findByStockIdOrderByChangedAtDescIdDesc(Long stockId);
}
