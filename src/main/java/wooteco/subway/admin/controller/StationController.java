package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        List<Station> stations = (List<Station>)stationRepository.findAll();
        for (Station station : stations) {
            if (station.getName().equals(view.getName())) {
                return ResponseEntity.badRequest().build();
            }
        }
        Station station = view.toStation();
        Station persistStation = stationRepository.save(station);

        return ResponseEntity
            .created(URI.create("/api/stations/" + persistStation.getId()))
            .build();
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
