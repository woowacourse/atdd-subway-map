package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        boolean existsName = stationDao.findByName(stationRequest.getName()).isPresent();
        if (existsName) {
            throw new DuplicateNameException("이미 저장된 역 이름입니다.");
        }

        Station newStation = stationDao.save(stationRequest);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                                                         .map(it -> new StationResponse(it.getId(), it.getName()))
                                                         .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
