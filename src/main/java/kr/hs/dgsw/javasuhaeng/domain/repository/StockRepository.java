//용인
package kr.hs.dgsw.javasuhaeng.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findAllByOrderByIdAsc();
    List<Stock> findByNameContainingIgnoreCaseOrderByIdAsc(String name);
    List<Stock> findBySectorOrderByIdAsc(Sector sector);
    List<Stock> findByNameContainingIgnoreCaseAndSectorOrderByIdAsc(String name, Sector sector);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.id = :id")
    Optional<Stock> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.sector = :sector ORDER BY s.id")
    List<Stock> findBySectorForUpdate(@Param("sector") Sector sector);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s ORDER BY s.id")
    List<Stock> findAllForUpdate();
}
