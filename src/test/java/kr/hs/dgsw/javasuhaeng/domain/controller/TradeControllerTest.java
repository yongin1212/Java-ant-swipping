package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.TradeTestSupport;
import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TradeControllerTest extends TradeTestSupport {

    @Autowired
    private MockMvc mockMvc;

    private String basicAuth;

    @BeforeEach
    void setUp() {
        Account account = createAccount(new BigDecimal("100000"));
        String credentials = account.getMember().getLoginId() + ":" + PASSWORD;
        basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("매수 성공: 201, 서버 현재가로 체결된 거래 정보 반환")
    void buy_created() throws Exception {
        BigDecimal price = stock(1L).getCurrentPrice();

        mockMvc.perform(authed(post("/api/trades/buy")).content("{\"stockId\":1,\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.type").value("BUY"))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.price").value(price.intValue()))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    @DisplayName("수량 1 미만 매수: 400 INVALID_INPUT")
    void buy_invalidQuantity() throws Exception {
        mockMvc.perform(authed(post("/api/trades/buy")).content("{\"stockId\":1,\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));
    }

    @Test
    @DisplayName("잔액 부족 매수: 400 INSUFFICIENT_CASH")
    void buy_insufficientCash() throws Exception {
        mockMvc.perform(authed(post("/api/trades/buy")).content("{\"stockId\":1,\"quantity\":1000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INSUFFICIENT_CASH"));
    }

    @Test
    @DisplayName("미보유 종목 매도: 404 HOLDING_NOT_FOUND")
    void sell_holdingNotFound() throws Exception {
        mockMvc.perform(authed(post("/api/trades/sell")).content("{\"stockId\":2,\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("HOLDING_NOT_FOUND"));
    }

    @Test
    @DisplayName("로그인 없이 매수: 401 UNAUTHORIZED")
    void buy_unauthorized() throws Exception {
        mockMvc.perform(post("/api/trades/buy").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\":1,\"quantity\":1}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("매수 후 거래 내역, 보유 주식, 총자산 조회: 200")
    void queries_afterBuy() throws Exception {
        mockMvc.perform(authed(post("/api/trades/buy")).content("{\"stockId\":1,\"quantity\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(authed(get("/api/trades")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
        mockMvc.perform(authed(get("/api/account/holdings")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].stockId").value(1))
                .andExpect(jsonPath("$.data[0].quantity").value(1));
        mockMvc.perform(authed(get("/api/account/assets")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAsset").value(100000))
                .andExpect(jsonPath("$.data.profit").value(0));
    }

    private MockHttpServletRequestBuilder authed(MockHttpServletRequestBuilder builder) {
        return builder.header(HttpHeaders.AUTHORIZATION, basicAuth).contentType(MediaType.APPLICATION_JSON);
    }
}
