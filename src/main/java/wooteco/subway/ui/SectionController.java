package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RequestMapping("/lines/{lineId}/sections")
@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping()
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @Valid @RequestBody SectionRequest sectionRequest) {
        sectionService.save(lineId, sectionRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);

        return ResponseEntity
                .ok()
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> lineNotFound() {
        return ResponseEntity
                .badRequest()
                .build();
    }
}
