//용인
package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.MarketEventDto;

import java.util.List;

public interface MarketEventService {
    List<MarketEventDto> findAll();
    MarketEventDto findById(Long id);
    MarketEventDto create(MarketEventDto dto);
    MarketEventDto update(Long id, MarketEventDto dto);
    void delete(Long id);
    MarketEventDto apply(Long id);
}
