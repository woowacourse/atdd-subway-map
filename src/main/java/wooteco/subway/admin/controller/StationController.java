package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;

@RestController
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/admin-station")
    public ModelAndView adminStation() {
        ModelAndView mv = new ModelAndView("admin-station");
        mv.addObject("stations", stationService.findAllStations());
        return mv;
    }

    @PostMapping("/stations")
    public ResponseEntity<?> createStation(
            @RequestBody StationCreateRequest request) {
        Station station = request.toStation();
        Station persistStation = stationService.create(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @GetMapping("/stations")
    public ResponseEntity<?> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
