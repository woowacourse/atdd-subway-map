package wooteco.subway.station.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.dto.Station;
import wooteco.subway.station.service.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<Station> createStation(@Valid @RequestBody Station stationRequest) {
        wooteco.subway.station.domain.Station station = new wooteco.subway.station.domain.Station(stationRequest.getName());
        wooteco.subway.station.domain.Station newStation = stationService.save(station);
        Station stationResponse = new Station(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Station>> showStations() {
        List<wooteco.subway.station.domain.Station> stations = stationService.findAll();
        List<Station> stationResponses = stations.stream()
                .map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id:[\\d]+}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
