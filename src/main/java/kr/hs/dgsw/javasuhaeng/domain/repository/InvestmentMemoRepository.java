package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentMemoRepository extends JpaRepository<InvestmentMemo, Long>, InvestmentMemoRepositoryCustom {
}
