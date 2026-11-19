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
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "holdings", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "stock_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holding extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private long quantity;

    public Holding(Account account, Stock stock) {
        this.account = account;
        this.stock = stock;
        this.quantity = 0;
    }

    public void increase(long quantity) {
        this.quantity += quantity;
    }

    public void decrease(long quantity) {
        if (this.quantity < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_QUANTITY);
        }
        this.quantity -= quantity;
    }

    public boolean isEmpty() {
        return quantity == 0;
    }

    // 평가금액 = 현재가 * 보유 수량
    public BigDecimal getEvaluationAmount() {
        return stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
