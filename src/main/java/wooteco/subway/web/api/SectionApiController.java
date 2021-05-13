package wooteco.subway.web.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.section.NotPositiveDistanceException;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;
import wooteco.subway.web.request.SectionRequest;
import wooteco.subway.web.response.SectionResponse;

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
            throw new NotPositiveDistanceException();
        }

        Station upStation = stationService.find(sectionRequest.getUpStationId());
        Station downStation = stationService.find(sectionRequest.getDownStationId());
        Section section =
                sectionService.create(Section.of(upStation, downStation, sectionRequest.getDistance()), lineId);

        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + section.getId())).body(SectionResponse.of(section));
    }

    @DeleteMapping
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
