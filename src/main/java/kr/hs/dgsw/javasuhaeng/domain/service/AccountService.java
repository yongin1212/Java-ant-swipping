package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.response.AccountResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.AssetResponse;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.HoldingResponse;

import java.util.List;

public interface AccountService {

    AccountResponse getAccount(Long memberId);

    List<HoldingResponse> getHoldings(Long memberId);

    AssetResponse getAssets(Long memberId);
}
