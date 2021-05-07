package wooteco.subway.section.web;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines/{lineId}/sections")
public class SectionApiController {

    private final SectionService sectionService;
    private final StationService stationService;


    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        Station upStation = stationService.findStation(sectionRequest.getUpStationId());
        Station downStation = stationService.findStation(sectionRequest.getDownStationId());
        Section section =
            sectionService.createSection(Section.of(upStation, downStation, sectionRequest.getDistance()), lineId);

        return ResponseEntity.created(URI.create("/lines/"+lineId+"/sections/"+section.getId())).body(SectionResponse.create(section));
    }

}
