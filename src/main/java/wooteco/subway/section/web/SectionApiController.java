package wooteco.subway.section.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.section.DistanceNotPositiveException;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationService;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionApiController {

    private final SectionService sectionService;
    private final StationService stationService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@RequestBody @Valid SectionRequest sectionRequest, BindingResult bindingResult, @PathVariable Long lineId) {
        if (bindingResult.hasErrors()) {
            throw new DistanceNotPositiveException();
        }

        Station upStation = stationService.find(sectionRequest.getUpStationId());
        Station downStation = stationService.find(sectionRequest.getDownStationId());
        Section section =
                sectionService.create(Section.create(upStation, downStation, sectionRequest.getDistance()), lineId);

        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + section.getId())).body(SectionResponse.create(section));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.removeByLineAndStationIds(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
