//용인
package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;

import java.util.List;

public interface StockRepositoryCustom {
    List<Stock> search(String name, Sector sector);
}
