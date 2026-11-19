package kr.hs.dgsw.javasuhaeng.domain.repository;

import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;

import java.util.List;

public interface InvestmentMemoRepositoryCustom {

    List<InvestmentMemo> search(Long memberId, String keyword, Long stockId);
}
