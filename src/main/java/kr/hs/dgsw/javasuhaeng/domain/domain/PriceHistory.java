//용인
package kr.hs.dgsw.javasuhaeng.domain.domain;

import jakarta.persistence.*;
import kr.hs.dgsw.javasuhaeng.global.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_histories")
@Getter
@NoArgsConstructor
public class PriceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private MarketEvent event;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal beforePrice;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal afterPrice;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public PriceHistory(Stock stock, MarketEvent event, BigDecimal beforePrice, BigDecimal afterPrice) {
        this.stock = stock;
        this.event = event;
        this.beforePrice = beforePrice;
        this.afterPrice = afterPrice;
        this.changedAt = LocalDateTime.now();
    }
}
