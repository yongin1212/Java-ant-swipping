package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.domain.Account;
import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Role;
import kr.hs.dgsw.javasuhaeng.domain.repository.AccountRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // HTTP Basic 로그인 헤더: "Basic " + base64("아이디:비밀번호")
    private final String login = "Basic " + Base64.getEncoder().encodeToString("tester:password".getBytes());

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(
                new Member("tester", passwordEncoder.encode("password"), "테스터", Role.USER));
        accountRepository.save(new Account(member, new BigDecimal("1000000")));
    }

    @Test
    @DisplayName("매수 성공: 201 Created")
    void buy_success() throws Exception {
        mockMvc.perform(post("/api/trades/buy")
                        .header("Authorization", login)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\": 1, \"quantity\": 10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.type").value("BUY"))
                .andExpect(jsonPath("$.data.totalAmount").value(100000));
    }

    @Test
    @DisplayName("매수 실패: 수량이 0이면 400 Bad Request")
    void buy_fail_zeroQuantity() throws Exception {
        mockMvc.perform(post("/api/trades/buy")
                        .header("Authorization", login)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\": 1, \"quantity\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));
    }

    @Test
    @DisplayName("매수 실패: 로그인하지 않으면 401 Unauthorized")
    void buy_fail_noLogin() throws Exception {
        mockMvc.perform(post("/api/trades/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\": 1, \"quantity\": 10}"))
                .andExpect(status().isUnauthorized());
    }
}
