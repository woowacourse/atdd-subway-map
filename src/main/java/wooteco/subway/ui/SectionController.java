package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

import java.util.NoSuchElementException;

@RequestMapping("/lines/{lineId}/sections")
@RestController
public class SectionController {
    private final SectionDao sectionDao;

    public SectionController(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @PostMapping()
    public ResponseEntity<Void> createStation(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());

        sectionDao.checkValid(section);

        sectionDao.save(section);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteStation(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionDao.delete(stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> lineNotFound() {
        return ResponseEntity.noContent().build();
    }
}
