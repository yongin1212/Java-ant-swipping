package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.request.WatchlistCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.WatchlistUpdateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.WatchlistResponse;

import java.util.List;

public interface WatchlistService {

    WatchlistResponse create(Long memberId, WatchlistCreateRequest request);

    List<WatchlistResponse> findAll(Long memberId);

    WatchlistResponse update(Long memberId, Long id, WatchlistUpdateRequest request);

    void delete(Long memberId, Long id);
}
