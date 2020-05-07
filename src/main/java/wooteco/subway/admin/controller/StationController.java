package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;

@RequestMapping("/station")
@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping()
    public ResponseEntity createStation(@RequestBody StationCreateRequest view) {
        Station station = view.toStation();
        Station persistStation = stationService.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @GetMapping()
    public ResponseEntity showStations() {
        return ResponseEntity.ok().body(stationService.showStations());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity deleteStation(@PathVariable String name) {
        stationService.deleteStationByName(name);
        return ResponseEntity.noContent().build();
    }
}
