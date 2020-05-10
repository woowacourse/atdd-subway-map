package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.Request;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.net.URI;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody Request<StationCreateRequest> view) {
        Station station = view.getContent().toStation();
        Station persistStation = stationRepository.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @GetMapping
    public ResponseEntity<Iterable<Station>> showStations() {
        return ResponseEntity.ok().body(stationRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StationResponse> deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
