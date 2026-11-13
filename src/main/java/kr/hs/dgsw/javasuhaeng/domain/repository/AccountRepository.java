package kr.hs.dgsw.javasuhaeng.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberId(Long memberId);

    // 매매 시 잔액 동시 변경 방지. 잠금 순서: Stock(id 오름차순) -> Account -> Holding
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.member.id = :memberId")
    Optional<Account> findByMemberIdForUpdate(@Param("memberId") Long memberId);
}
