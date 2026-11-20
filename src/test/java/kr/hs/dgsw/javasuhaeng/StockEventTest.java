//용인
package kr.hs.dgsw.javasuhaeng;

import kr.hs.dgsw.javasuhaeng.domain.service.MarketEventService;
import kr.hs.dgsw.javasuhaeng.domain.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StockEventTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MarketEventService eventService;

    @Autowired
    private StockService stockService;

    @Test
    void 종목_조회_성공() throws Exception {
        mvc.perform(get("/api/stocks/1").header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("한빛전자"));
    }

    @Test
    void 이벤트_적용_성공() {
        assertThat(eventService.apply(1L).getStatus()).isEqualTo("APPLIED");
        assertThat(stockService.getStock(1L).currentPrice()).isEqualByComparingTo("11000");
    }

    @Test
    void 없는_종목_조회_실패() throws Exception {
        mvc.perform(get("/api/stocks/99999").header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("STOCK_NOT_FOUND"));
    }
}
