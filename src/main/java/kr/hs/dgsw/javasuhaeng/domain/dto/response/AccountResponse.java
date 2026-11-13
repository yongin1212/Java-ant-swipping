package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;

import java.math.BigDecimal;

public record AccountResponse(Long accountId, BigDecimal initialCash, BigDecimal cash) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getInitialCash(), account.getCash());
    }
}
