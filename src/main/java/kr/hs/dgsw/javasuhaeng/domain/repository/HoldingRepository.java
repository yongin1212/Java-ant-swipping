package kr.hs.dgsw.javasuhaeng.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hs.dgsw.javasuhaeng.domain.domain.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    Optional<Holding> findByAccountIdAndStockId(Long accountId, Long stockId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM Holding h WHERE h.account.id = :accountId AND h.stock.id = :stockId")
    Optional<Holding> findByAccountIdAndStockIdForUpdate(@Param("accountId") Long accountId,
                                                         @Param("stockId") Long stockId);
}
