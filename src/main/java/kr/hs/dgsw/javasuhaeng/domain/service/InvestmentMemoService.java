package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoUpdateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.InvestmentMemoResponse;

import java.util.List;

public interface InvestmentMemoService {

    InvestmentMemoResponse create(Long memberId, InvestmentMemoCreateRequest request);

    List<InvestmentMemoResponse> findAll(Long memberId);

    InvestmentMemoResponse findById(Long memberId, Long id);

    InvestmentMemoResponse update(Long memberId, Long id, InvestmentMemoUpdateRequest request);

    void delete(Long memberId, Long id);
}
