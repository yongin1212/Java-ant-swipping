//용인
package kr.hs.dgsw.javasuhaeng.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.hs.dgsw.javasuhaeng.domain.domain.QStock;
import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Stock> search(String name, Sector sector) {
        QStock stock = QStock.stock;
        BooleanBuilder condition = new BooleanBuilder();

        if (name != null && !name.isBlank()) {
            condition.and(stock.name.containsIgnoreCase(name.trim()));
        }
        if (sector != null) {
            condition.and(stock.sector.eq(sector));
        }

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        return query.selectFrom(stock)
                .where(condition)
                .orderBy(stock.id.asc())
                .fetch();
    }
}
