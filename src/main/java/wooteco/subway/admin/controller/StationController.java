package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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

    @GetMapping("/admin-station")
    public ModelAndView adminStation() {
        ModelAndView mv = new ModelAndView("admin-station");
        mv.addObject("stations", StationResponse.listOf(stationRepository.findAll()));
        return mv;
    }

    @PostMapping("/stations")
    public ResponseEntity<?> createStation(
            @RequestBody StationCreateRequest request) {
        Station station = request.toStation();
        Station persistStation = stationRepository.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @GetMapping("/stations")
    public ResponseEntity<?> showStations() {
        List<Station> stations = stationRepository.findAll();

        return ResponseEntity.ok().body(StationResponse.listOf(stations));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
