package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest view) {
        Station station = view.toStation();
        Station persistStation = stationRepository.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationRepository.findAll();
        return ResponseEntity.ok()
                .body(StationResponse.listOf(stations));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<StationResponse> deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
