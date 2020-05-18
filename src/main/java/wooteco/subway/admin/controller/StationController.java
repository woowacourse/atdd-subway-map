package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping
    public ResponseEntity<Void> createStation(@RequestBody StationCreateRequest view) {
        Station station = view.toStation();
        Station persistStation = stationRepository.save(station);

        return ResponseEntity
            .created(URI.create("/api/stations/" + persistStation.getId()))
            .build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> catchException() {
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<Iterable<Station>> showStations() {
        return ResponseEntity.ok().body(stationRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stationInfo")
    public ResponseEntity<StationResponse> getStationIdByName(@RequestParam(name = "name") String name) {
        Station station = stationRepository.findIdByName(name);
        return ResponseEntity
            .ok()
            .body(StationResponse.of(station));
    }
}
