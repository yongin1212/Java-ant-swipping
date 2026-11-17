//용인
package kr.hs.dgsw.javasuhaeng.domain.dto;

import kr.hs.dgsw.javasuhaeng.domain.domain.MarketEvent;
import kr.hs.dgsw.javasuhaeng.domain.domain.Sector;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class MarketEventDto {

    private Long id;
    private String title;
    private String description;
    private String targetType;
    private Long stockId;
    private Sector sector;
    private BigDecimal changeRate;
    private String status;
    private LocalDateTime appliedAt;

    public static MarketEventDto from(MarketEvent event) {
        MarketEventDto dto = new MarketEventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setTargetType(event.getTargetType());
        if (event.getTargetStock() != null) {
            dto.setStockId(event.getTargetStock().getId());
        }
        dto.setSector(event.getTargetSector());
        dto.setChangeRate(event.getChangeRate());
        dto.setStatus(event.getStatus());
        dto.setAppliedAt(event.getAppliedAt());
        return dto;
    }
}
