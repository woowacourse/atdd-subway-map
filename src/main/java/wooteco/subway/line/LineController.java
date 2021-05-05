package wooteco.subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        List<Station> stations = new ArrayList<>();
        Station upStation = StationDao.findById(lineRequest.getUpStationId()).get();
        Station downStation = StationDao.findById(lineRequest.getDownStationId()).get();
        stations.add(upStation);
        stations.add(downStation);

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), stations);

        Line newLine = LineDao.save(line);

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = LineDao.findAll();

        List<LineResponse> lineResponses = lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable long id) {
        Line line = LineDao.findById(id).get();

        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), generateStationResponse(line.getStations()));

        return ResponseEntity.ok().body(lineResponse);
    }

    private List<StationResponse> generateStationResponse(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

}
