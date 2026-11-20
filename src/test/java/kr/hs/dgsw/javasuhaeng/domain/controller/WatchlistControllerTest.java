package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.repository.MemberRepository;
import kr.hs.dgsw.javasuhaeng.domain.repository.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WatchlistRepository watchlistRepository;

    private Long memberId;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.findByLoginId("user1").orElseThrow().getId();
        headers = new HttpHeaders();
        headers.setBasicAuth("user1", "password");
    }

    @Test
    @DisplayName("Basic 인증 후 관심종목 등록: 201, 인증한 회원의 데이터로 저장된다")
    void create_created() throws Exception {
        mockMvc.perform(post("/api/watchlist")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\":1,\"targetPrice\":12000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stockId").value(1))
                .andExpect(jsonPath("$.data.targetPrice").value(12000))
                .andExpect(jsonPath("$.error").doesNotExist());

        assertThat(watchlistRepository.findAllByMemberId(memberId))
                .singleElement().satisfies(item -> {
                    assertThat(item.getStock().getId()).isEqualTo(1L);
                    assertThat(item.getTargetPrice()).isEqualByComparingTo("12000");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "12.5", "10000000000000000000"})
    @DisplayName("잘못된 목표 가격: 400 INVALID_INPUT, 관심종목을 저장하지 않는다")
    void create_invalidTargetPrice(String targetPrice) throws Exception {
        mockMvc.perform(post("/api/watchlist")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\":1,\"targetPrice\":%s}".formatted(targetPrice)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));

        assertThat(watchlistRepository.findAllByMemberId(memberId)).isEmpty();
    }

    @Test
    @DisplayName("소수 종목 ID로 관심종목 등록: 400 INVALID_INPUT, 저장하지 않는다")
    void create_decimalStockId() throws Exception {
        long beforeCount = watchlistRepository.count();

        mockMvc.perform(post("/api/watchlist")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockId\":1.9,\"targetPrice\":12000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));

        assertThat(watchlistRepository.count()).isEqualTo(beforeCount);
    }

    @Test
    @DisplayName("인증 없이 관심종목 조회: 401 UNAUTHORIZED")
    void findAll_unauthorized() throws Exception {
        mockMvc.perform(get("/api/watchlist"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }
}
