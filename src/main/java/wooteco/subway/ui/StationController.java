package wooteco.subway.ui;

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
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.StringFormat;

@RequestMapping("/stations")
@RestController
public class StationController {

    private static final String STATION_DUPLICATION_EXCEPTION_MESSAGE = "중복되는 지하철역이 존재합니다.";
    private static final String NO_SUCH_STATION_EXCEPTION_MESSAGE = "해당 ID의 지하철역이 존재하지 않습니다.";

    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        if (stationDao.findByName(stationRequest.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    StringFormat.errorMessage(stationRequest.getName(), STATION_DUPLICATION_EXCEPTION_MESSAGE));
        }
        Station station = stationRequest.toEntity();
        Station newStation = stationDao.save(station);
        StationResponse stationResponse = StationResponse.of(newStation);
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        Station station = stationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        StringFormat.errorMessage(id, NO_SUCH_STATION_EXCEPTION_MESSAGE)));
        stationDao.delete(station);
        return ResponseEntity.noContent().build();
    }
}
