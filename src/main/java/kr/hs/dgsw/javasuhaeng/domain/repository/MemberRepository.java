package kr.hs.dgsw.javasuhaeng.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginId(String loginId);
}
