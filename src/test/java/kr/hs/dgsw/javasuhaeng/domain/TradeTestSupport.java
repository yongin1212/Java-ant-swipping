package kr.hs.dgsw.javasuhaeng.domain;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Role;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.repository.AccountRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.HoldingRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.UUID;

// 매매 테스트 공통 준비: 테스트마다 새 회원과 계좌를 만들어 다른 테스트 데이터와 섞이지 않게 한다.
public abstract class TradeTestSupport {

    protected static final String PASSWORD = "password";

    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected StockRepository stockRepository;
    @Autowired
    protected HoldingRepository holdingRepository;
    @Autowired
    protected TradeRepository tradeRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected Account createAccount(BigDecimal initialCash) {
        String loginId = "t" + UUID.randomUUID().toString().substring(0, 8);
        Member member = memberRepository.save(
                new Member(loginId, passwordEncoder.encode(PASSWORD), "테스트회원", Role.USER));
        return accountRepository.save(new Account(member, initialCash));
    }

    protected Stock stock(Long stockId) {
        return stockRepository.findById(stockId).orElseThrow();
    }

    // 트랜잭션 없이 커밋된 데이터를 쓰는 테스트용 정리
    protected void deleteAccount(Account account) {
        tradeRepository.deleteAll(tradeRepository.findAllByAccountIdOrderByTradedAtDescIdDesc(account.getId()));
        holdingRepository.deleteAll(holdingRepository.findAllWithStockByAccountId(account.getId()));
        accountRepository.deleteById(account.getId());
        memberRepository.deleteById(account.getMember().getId());
    }
}
