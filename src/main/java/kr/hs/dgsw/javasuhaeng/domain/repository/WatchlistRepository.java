package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.WatchlistItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    boolean existsByMemberIdAndStockId(Long memberId, Long stockId);

    @EntityGraph(attributePaths = "stock")
    List<WatchlistItem> findAllByMemberId(Long memberId);
}
