package wooteco.subway.web.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.dao.StationDao;
import wooteco.subway.web.dto.StationRequest;
import wooteco.subway.web.dto.StationResponse;

@RestController
@RequestMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
public class StationController {

    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(
            @RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Long id = stationDao.save(station);
        Station newStation = new Station(id, station.getName());

        StationResponse stationResponse = new StationResponse(newStation.getId(),
                newStation.getName());

        return ResponseEntity
                .created(URI.create("/stations/" + newStation.getId()))
                .body(stationResponse);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationDao.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
