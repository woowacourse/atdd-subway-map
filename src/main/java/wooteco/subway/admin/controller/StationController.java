package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private static long NO_ID = 0L;

    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody @Valid StationCreateRequest view) {
        Station station = view.toStation();
        Station persistStation = stationService.create(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> showStations() {
        return ResponseEntity.ok().body(stationService.showStations());
    }

    @GetMapping("/stations/id/")
    public ResponseEntity<Long> findIdByName(HttpServletRequest request) {
        String name = request.getParameter("name");
        if (name.equals("")) {
            return ResponseEntity.ok().body(NO_ID);
        }
        return ResponseEntity.ok().body(stationService.findIdByName(name));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
