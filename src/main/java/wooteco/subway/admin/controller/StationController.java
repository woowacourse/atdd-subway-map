package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;
import wooteco.subway.admin.service.StationService;

@RestController
public class StationController {
    private final StationService stationService;
    private final StationRepository stationRepository;

    public StationController(StationService stationService, StationRepository stationRepository) {
        this.stationService = stationService;
        this.stationRepository = stationRepository;
    }

    @GetMapping("/stations")
    public ResponseEntity showStations() {
        return ResponseEntity.ok().body(stationRepository.findAll());
    }

    @GetMapping("/stations/{id}")
    public StationResponse getStation(@PathVariable Long id) {
        Station station = stationRepository.findById(id).get();
        return StationResponse.of(station);
    }

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody StationCreateRequest view) {
        Long savedId = stationService.save(view);

        return ResponseEntity
            .created(URI.create("/stations/" + savedId))
            .body(savedId);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
