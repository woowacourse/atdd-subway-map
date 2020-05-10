package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class StationController {
    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(StationResponse.of(stationRepository.findAll()));
    }

    @GetMapping("/stations/{name}")
    public ResponseEntity<StationResponse> showStationByName(@PathVariable String name) {
        if (name.isEmpty()) {
            return ResponseEntity.ok().body(null);
        }
        Station station = stationRepository.findByName(name).orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok().body(StationResponse.of(station));
    }

    @PostMapping("/stations")
    public ResponseEntity<Object> createStation(@RequestBody StationCreateRequest view) {
        Station station = view.toStation();
        Station persistStation = stationRepository.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId())).build();
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Object> deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
