//용인
package kr.hs.dgsw.javasuhaeng.domain.controller;

import kr.hs.dgsw.javasuhaeng.domain.dto.MarketEventDto;
import kr.hs.dgsw.javasuhaeng.domain.service.MarketEventService;
import kr.hs.dgsw.javasuhaeng.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MarketEventController {

    private final MarketEventService eventService;

    @GetMapping("/api/events")
    public ApiResponse<List<MarketEventDto>> findAll() {
        return ApiResponse.ok(eventService.findAll());
    }

    @GetMapping("/api/events/{id}")
    public ApiResponse<MarketEventDto> findById(@PathVariable Long id) {
        return ApiResponse.ok(eventService.findById(id));
    }

    @PostMapping("/api/admin/events")
    public ResponseEntity<ApiResponse<MarketEventDto>> create(@RequestBody MarketEventDto dto) {
        MarketEventDto result = eventService.create(dto);
        return ResponseEntity.status(201).body(ApiResponse.ok(result));
    }

    @PutMapping("/api/admin/events/{id}")
    public ApiResponse<MarketEventDto> update(@PathVariable Long id, @RequestBody MarketEventDto dto) {
        return ApiResponse.ok(eventService.update(id, dto));
    }

    @DeleteMapping("/api/admin/events/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
