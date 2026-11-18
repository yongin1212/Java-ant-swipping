//용인
package kr.hs.dgsw.javasuhaeng.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hs.dgsw.javasuhaeng.domain.domain.MarketEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarketEventRepository extends JpaRepository<MarketEvent, Long> {
    List<MarketEvent> findAllByOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM MarketEvent e WHERE e.id = :id")
    Optional<MarketEvent> findByIdForUpdate(@Param("id") Long id);
}
