package wooteco.subway.station.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationDto;
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
    public ResponseEntity<StationDto> createStation(@Valid @RequestBody StationDto stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationService.save(station);
        StationDto stationDto = new StationDto(newStation.id(), newStation.name());
        return ResponseEntity.created(URI.create("/stations/" + newStation.id())).body(stationDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationDto>> showStations() {
        List<Station> stations = stationService.findAll();
        List<StationDto> stationRespons = stations.stream()
                .map(it -> new StationDto(it.id(), it.name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationRespons);
    }

    @DeleteMapping("/{id:[\\d]+}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
