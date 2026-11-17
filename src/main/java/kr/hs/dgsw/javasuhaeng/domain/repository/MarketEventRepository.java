//용인
package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.MarketEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketEventRepository extends JpaRepository<MarketEvent, Long> {
    List<MarketEvent> findAllByOrderByIdDesc();
}
