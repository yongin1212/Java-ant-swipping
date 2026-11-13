package kr.hs.dgsw.javasuhaeng.domain.service.impl;

import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoCreateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.request.InvestmentMemoUpdateRequest;
import kr.hs.dgsw.javasuhaeng.domain.dto.response.InvestmentMemoResponse;
import kr.hs.dgsw.javasuhaeng.domain.repository.InvestmentMemoRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import kr.hs.dgsw.javasuhaeng.domain.service.InvestmentMemoService;
import kr.hs.dgsw.javasuhaeng.global.exception.BusinessException;
import kr.hs.dgsw.javasuhaeng.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestmentMemoServiceImpl implements InvestmentMemoService {

    private final InvestmentMemoRepository investmentMemoRepository;
    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;

    @Override
    public InvestmentMemoResponse create(Long memberId, InvestmentMemoCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElse(null);
        if (member == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Stock stock = stockRepository.findById(request.stockId())
                .orElse(null);
        if (stock == null) {
            throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        }

        InvestmentMemo memo = new InvestmentMemo(member, stock, request.title(), request.content());
        return InvestmentMemoResponse.from(investmentMemoRepository.saveAndFlush(memo));
    }

    @Override
    public List<InvestmentMemoResponse> findAll(Long memberId) {
        List<InvestmentMemo> memos = investmentMemoRepository.findAllByMemberId(memberId);
        return memos.stream()
                .map(memo -> InvestmentMemoResponse.from(memo))
                .toList();
    }

    @Override
    public InvestmentMemoResponse findById(Long memberId, Long id) {
        return InvestmentMemoResponse.from(findOwnedMemo(memberId, id));
    }

    @Override
    public InvestmentMemoResponse update(Long memberId, Long id, InvestmentMemoUpdateRequest request) {
        InvestmentMemo memo = findOwnedMemo(memberId, id);
        memo.update(request.title(), request.content());
        investmentMemoRepository.saveAndFlush(memo);
        return InvestmentMemoResponse.from(memo);
    }

    @Override
    public void delete(Long memberId, Long id) {
        InvestmentMemo memo = findOwnedMemo(memberId, id);
        investmentMemoRepository.delete(memo);
    }

    private InvestmentMemo findOwnedMemo(Long memberId, Long id) {
        InvestmentMemo memo = investmentMemoRepository.findById(id)
                .orElse(null);
        if (memo == null) {
            throw new BusinessException(ErrorCode.MEMO_NOT_FOUND);
        }
        if (!memo.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return memo;
    }
}
