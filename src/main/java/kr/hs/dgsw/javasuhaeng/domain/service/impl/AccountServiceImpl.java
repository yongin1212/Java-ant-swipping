package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Holding;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.AccountResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.AssetResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.HoldingResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.AccountRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.HoldingRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.AccountService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;

    @Override
    public AccountResponse getAccount(Long memberId) {
        return AccountResponse.from(findAccount(memberId));
    }

    @Override
    public List<HoldingResponse> getHoldings(Long memberId) {
        Account account = findAccount(memberId);
        return holdingRepository.findAllWithStockByAccountId(account.getId()).stream()
                .map(HoldingResponse::from)
                .toList();
    }

    @Override
    public AssetResponse getAssets(Long memberId) {
        Account account = findAccount(memberId);
        BigDecimal stockEvaluation = holdingRepository.findAllWithStockByAccountId(account.getId()).stream()
                .map(Holding::getEvaluationAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return AssetResponse.of(account, stockEvaluation);
    }

    private Account findAccount(Long memberId) {
        return accountRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }
}
