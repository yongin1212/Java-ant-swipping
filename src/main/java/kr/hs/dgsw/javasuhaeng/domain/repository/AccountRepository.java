package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberId(Long memberId);
}
