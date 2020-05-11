package wooteco.subway.admin.controller;

import java.net.URI;

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

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping()
    public ResponseEntity showStations() {
        return ResponseEntity.ok().body(stationService.showStations());
    }

    @PostMapping()
    public ResponseEntity createStation(@RequestBody StationCreateRequest stationCreateRequest) {
        Station station = stationCreateRequest.toStation();
        Station persistStation = stationService.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}")
    public ResponseEntity getStation(@PathVariable String name) {
        Station station = stationService.findByName(name);
        return ResponseEntity.ok().body(station.getId());
    }
}
