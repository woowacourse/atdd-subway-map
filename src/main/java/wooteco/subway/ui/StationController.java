package wooteco.subway.ui;

import static wooteco.subway.ui.StationController.STATIONS_API_URI;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@RestController
@RequestMapping(STATIONS_API_URI)
public class StationController {

    public static final String STATIONS_API_URI = "/stations";

    private final StationDao stationDao;

    public StationController(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Long id = stationDao.save(station);
        StationResponse stationResponse = new StationResponse(id, station.getName());
        return ResponseEntity.created(URI.create(STATIONS_API_URI + "/" + id)).body(stationResponse);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
