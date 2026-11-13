package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.AccountResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.AccountRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.AccountService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponse getAccount(Long memberId) {
        Account account = accountRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        return AccountResponse.from(account);
    }
}
