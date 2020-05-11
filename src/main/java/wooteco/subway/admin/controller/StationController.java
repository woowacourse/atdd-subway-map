package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

@RequestMapping("/stations")
@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping()
    public ResponseEntity<StationResponse> createStation(@RequestBody @Valid StationCreateRequest stationCreateRequest) {
        Station station = stationCreateRequest.toStation();
        Station persistStation = stationService.addStation(station);

        return ResponseEntity
            .created(URI.create(String.valueOf(persistStation.getId())))
            .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> findStationById(@PathVariable Long id) {
        Station station = stationService.findStationById(id);

        return ResponseEntity
            .ok()
            .body(station);
    }

    @GetMapping()
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.showStations();
        List<StationResponse> stationResponses = stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());

        return ResponseEntity
            .ok()
            .body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.removeStation(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
