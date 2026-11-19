package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TradeResponse;

public interface TradeService {

    TradeResponse buy(Long memberId, TradeRequest request);
}
