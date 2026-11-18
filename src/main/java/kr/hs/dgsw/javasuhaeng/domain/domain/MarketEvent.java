//용인
package kr.hs.dgsw.javasuhaeng.domain.domain;

import jakarta.persistence.*;
import kr.hs.dgsw.javasuhaeng.global.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_events")
@Getter
@NoArgsConstructor
public class MarketEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String targetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_stock_id")
    private Stock targetStock;

    @Enumerated(EnumType.STRING)
    private Sector targetSector;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal changeRate;

    @Column(nullable = false)
    private String status;

    private LocalDateTime appliedAt;

    public MarketEvent(String title, String description, String targetType,
                       Stock targetStock, Sector targetSector, BigDecimal changeRate) {
        this.title = title;
        this.description = description;
        this.targetType = targetType;
        this.targetStock = targetStock;
        this.targetSector = targetSector;
        this.changeRate = changeRate;
        this.status = "PENDING";
    }

    public void update(String title, String description, String targetType,
                       Stock targetStock, Sector targetSector, BigDecimal changeRate) {
        this.title = title;
        this.description = description;
        this.targetType = targetType;
        this.targetStock = targetStock;
        this.targetSector = targetSector;
        this.changeRate = changeRate;
    }

    public void markApplied() {
        this.status = "APPLIED";
        this.appliedAt = LocalDateTime.now();
    }
}
