package wooteco.subway.station.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private static final Logger log = LoggerFactory.getLogger("console");
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse newStation = stationService.save(stationRequest);
        log.info(newStation.getName() + "역이 생성되었습니다.");
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(newStation);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> allStations = stationService.findAllStations();
        log.info("등록된 지하철 역 조회 성공");
        return ResponseEntity.ok().body(allStations);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        log.info("지하철 역 삭제 성공");
        return ResponseEntity.noContent().build();
    }
}
