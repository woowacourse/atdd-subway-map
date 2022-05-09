package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

@Controller
@RequestMapping("/stations")
public class StationController {
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "지하철 노선에 해당 역이 등록되어있어 역을 삭제할 수 없습니다.";
    private final StationService stationService;
    private final SectionService sectionService;

    private final LineService lineService;

    public StationController(StationService stationService, SectionService sectionService,
                             LineService lineService) {
        this.stationService = stationService;
        this.sectionService = sectionService;
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station newStation = stationService.save(stationRequest.toEntity());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
                .body(StationResponse.of(newStation));
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok()
                .body(stationService.findAll()
                        .stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        //TODO
        for (Line line : lineService.findAll()) {
            validateStationNotInLine(id, line.getId());
        }
        return ResponseEntity.noContent().build();
    }

    private void validateStationNotInLine(Long id, Long lineId) {
        if(sectionService.findStationsOfLine(lineId).contains(stationService.findById(id))) {
            throw new IllegalArgumentException(ALREADY_IN_LINE_ERROR_MESSAGE);
        }
    }
}
