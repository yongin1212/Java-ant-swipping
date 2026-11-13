package kr.hs.dgsw.javasuhaeng.domain.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.hs.dgsw.javasuhaeng.global.common.BaseEntity;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal initialCash;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal cash;

    public Account(Member member, BigDecimal initialCash) {
        this.member = member;
        this.initialCash = initialCash;
        this.cash = initialCash;
    }

    public void withdraw(BigDecimal amount) {
        if (cash.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_CASH);
        }
        this.cash = cash.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.cash = cash.add(amount);
    }
}
