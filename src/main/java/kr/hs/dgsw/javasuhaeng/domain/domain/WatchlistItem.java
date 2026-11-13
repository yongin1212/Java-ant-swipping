package kr.hs.dgsw.javasuhaeng.domain.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.hs.dgsw.javasuhaeng.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "watchlist_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_watchlist_member_stock", columnNames = {"member_id", "stock_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WatchlistItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false, updatable = false)
    private Stock stock;

    @Column(precision = 19, scale = 0)
    private BigDecimal targetPrice;

    public WatchlistItem(Member member, Stock stock, BigDecimal targetPrice) {
        this.member = member;
        this.stock = stock;
        this.targetPrice = targetPrice;
    }

    public void updateTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }
}
