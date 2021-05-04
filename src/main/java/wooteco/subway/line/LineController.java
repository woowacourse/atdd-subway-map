package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(
                URI.create("/lines/" + lineResponse.getId())
        ).body(lineResponse);
    }

//    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<StationResponse>> showStations() {
//        List<Station> stations = StationDao.findAll();
//        List<StationResponse> stationResponses = stations.stream()
//                .map(it -> new StationResponse(it.getId(), it.getName()))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok().body(stationResponses);
//    }
//
//    @DeleteMapping("/stations/{id}")
//    public ResponseEntity deleteStation(@PathVariable Long id) {
//        return ResponseEntity.noContent().build();
//    }
}
