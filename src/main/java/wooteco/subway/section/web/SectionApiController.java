package wooteco.subway.section.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.section.DistanceNotPositiveException;
import wooteco.subway.line.LineService;
import wooteco.subway.section.SectionService;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionApiController {

    private final SectionService sectionService;
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@RequestBody @Valid SectionRequest sectionRequest, BindingResult bindingResult, @PathVariable Long lineId) {
        if (bindingResult.hasErrors()) {
            throw new DistanceNotPositiveException();
        }
        lineService.validateExistLineById(lineId);

        SectionResponse sectionResponse = sectionService.create(sectionRequest, lineId);

        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + sectionResponse.getId())).body(sectionResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.removeByLineAndStationIds(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
