package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationDao stationDao;

    public StationController(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody final StationRequest stationRequest) {
        final String name = stationRequest.getName();

        if (stationDao.isDuplicatedName(name)) {
            throw new StationException("이미 존재하는 역 이름입니다.");
        }

        final Long id = stationDao.save(name);

        final StationResponse stationResponse = new StationResponse(id, name);
        return ResponseEntity.created(URI.create("/stations/" + id)).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        final List<Station> stations = stationDao.findAll();

        final List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable final Long id) {
        stationDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
