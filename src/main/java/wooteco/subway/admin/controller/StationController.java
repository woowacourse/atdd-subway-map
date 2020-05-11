package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;

@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody StationCreateRequest request) {
        StationResponse response = stationService.createStation(request);

        return ResponseEntity
                .created(URI.create("/stations/" + response.getId()))
                .body(response);
    }

    @GetMapping("/stations")
    public ResponseEntity showStations() {
        return ResponseEntity.ok().body(stationService.findAll());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable("id") Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
