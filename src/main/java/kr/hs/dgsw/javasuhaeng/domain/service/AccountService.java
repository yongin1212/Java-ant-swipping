package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.response.AccountResponse;

public interface AccountService {

    AccountResponse getAccount(Long memberId);
}
