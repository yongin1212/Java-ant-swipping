//용인
package kr.hs.dgsw.javasuhaeng.domain.dto;

import kr.hs.dgsw.javasuhaeng.domain.domain.PriceHistory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PriceHistoryDto {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private BigDecimal beforePrice;
    private BigDecimal afterPrice;
    private LocalDateTime changedAt;

    public static PriceHistoryDto from(PriceHistory history) {
        PriceHistoryDto dto = new PriceHistoryDto();
        dto.setId(history.getId());
        dto.setEventId(history.getEvent().getId());
        dto.setEventTitle(history.getEvent().getTitle());
        dto.setBeforePrice(history.getBeforePrice());
        dto.setAfterPrice(history.getAfterPrice());
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }
}
