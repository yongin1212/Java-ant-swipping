package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.request.TradeRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.TradeResponse;

import java.util.List;

public interface TradeService {

    TradeResponse buy(Long memberId, TradeRequest request);

    TradeResponse sell(Long memberId, TradeRequest request);

    List<TradeResponse> getTrades(Long memberId);
}
