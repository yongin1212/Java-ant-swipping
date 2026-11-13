package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findAllByOrderByIdAsc();
    List<Stock> findByNameContainingIgnoreCaseOrderByIdAsc(String name);
    List<Stock> findBySectorOrderByIdAsc(Sector sector);
    List<Stock> findByNameContainingIgnoreCaseAndSectorOrderByIdAsc(String name, Sector sector);
}
