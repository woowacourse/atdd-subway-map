package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

@RequestMapping("/stations")
@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping()
    public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest stationCreateRequest) {
        Station station = stationCreateRequest.toStation();
        Station persistStation = stationService.addStation(station);

        return ResponseEntity
            .created(URI.create("/stations/" + persistStation.getId()))
            .body(StationResponse.of(persistStation));
    }

    @GetMapping("/{name}")
    public ResponseEntity<Long> findStation(@PathVariable String name) {
        Station station = stationService.findStationByName(name);
        return ResponseEntity.ok().body(station.getId());
    }

    @GetMapping()
    public ResponseEntity<List<Station>> showStations() {
        return ResponseEntity.ok().body(stationService.showStations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStation(@PathVariable Long id) {
        stationService.removeStation(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<Model> dbException(Exception e, Model model) {
        model.addAttribute("error", "중복된 이름을 넣었습니다!");
        return ResponseEntity.badRequest().body(model);
    }

}
