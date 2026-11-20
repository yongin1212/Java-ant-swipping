package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.domain.InvestmentMemo;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Stock;
import kr.hs.dgsw.javasuhaeng.domain.repository.InvestmentMemoRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InvestmentMemoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private InvestmentMemoRepository investmentMemoRepository;

    private Member member;
    private Stock stock;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        member = memberRepository.findByLoginId("user1").orElseThrow();
        stock = stockRepository.findById(1L).orElseThrow();
        headers = new HttpHeaders();
        headers.setBasicAuth("user1", "password");
    }

    @Test
    @DisplayName("정수 종목 ID로 메모 등록: 201, 메모가 저장된다")
    void create_created() throws Exception {
        long beforeCount = investmentMemoRepository.count();

        mockMvc.perform(post("/api/memos")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\":1,\"title\":\"실적 발표\",\"content\":\"매수 예정\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stockId").value(1))
                .andExpect(jsonPath("$.data.title").value("실적 발표"))
                .andExpect(jsonPath("$.data.content").value("매수 예정"));

        assertThat(investmentMemoRepository.count()).isEqualTo(beforeCount + 1);
    }

    @Test
    @DisplayName("소수 종목 ID로 메모 등록: 400 INVALID_INPUT, 저장하지 않는다")
    void create_decimalStockId() throws Exception {
        long beforeCount = investmentMemoRepository.count();

        mockMvc.perform(post("/api/memos")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\":1.9,\"title\":\"실적 발표\",\"content\":\"매수 예정\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));

        assertThat(investmentMemoRepository.count()).isEqualTo(beforeCount);
    }

    @Test
    @DisplayName("메모 검색: 검색어와 종목 조건을 모두 적용하고 다른 회원의 메모는 제외한다")
    void findAll_keywordAndStockId() throws Exception {
        InvestmentMemo expected = investmentMemoRepository.saveAndFlush(
                new InvestmentMemo(member, stock, "실적 발표", "실적을 확인하고 매수 예정"));
        investmentMemoRepository.saveAndFlush(new InvestmentMemo(member, stock, "정책 발표", "관망 예정"));
        Stock otherStock = stockRepository.findById(2L).orElseThrow();
        investmentMemoRepository.saveAndFlush(new InvestmentMemo(member, otherStock, "실적 발표", "다른 종목 메모"));
        Member otherMember = memberRepository.findByLoginId("user2").orElseThrow();
        investmentMemoRepository.saveAndFlush(new InvestmentMemo(otherMember, stock, "실적 발표", "다른 회원 메모"));

        mockMvc.perform(get("/api/memos")
                        .headers(headers)
                        .param("keyword", "실적")
                        .param("stockId", stock.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(expected.getId().intValue()))
                .andExpect(jsonPath("$.data[0].stockId").value(stock.getId().intValue()))
                .andExpect(jsonPath("$.data[0].title").value("실적 발표"))
                .andExpect(jsonPath("$.data[0].content").value("실적을 확인하고 매수 예정"));
    }

    @Test
    @DisplayName("종목 ID에 문자를 전달한 메모 검색: 400 INVALID_INPUT")
    void findAll_invalidStockId() throws Exception {
        mockMvc.perform(get("/api/memos")
                        .headers(headers)
                        .param("stockId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));
    }
}
