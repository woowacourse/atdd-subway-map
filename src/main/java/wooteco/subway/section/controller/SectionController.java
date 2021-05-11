package wooteco.subway.section.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.line.controller.LineResponse;
import wooteco.subway.section.exception.WrongSectionInformationExcetpion;
import wooteco.subway.section.service.SectionService;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody @Valid SectionRequest sectionRequest,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new WrongSectionInformationExcetpion("요청이 올바르지 않습니다.");
        }
        sectionService.save(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return ResponseEntity.created(URI.create("/lines/" + lineId)).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId, @RequestParam long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
