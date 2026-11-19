package kr.hs.dgsw.javasuhaeng.domain.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.hs.dgsw.javasuhaeng.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TradeType type;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime tradedAt;

    public Trade(Account account, Stock stock, TradeType type, int quantity, BigDecimal price) {
        this.account = account;
        this.stock = stock;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = price.multiply(BigDecimal.valueOf(quantity));
        this.tradedAt = LocalDateTime.now();
    }
}
