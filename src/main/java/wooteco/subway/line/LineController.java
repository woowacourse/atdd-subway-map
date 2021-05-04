package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        List<Station> stations = new ArrayList<>();
        Station upStation = StationDao.findById(lineRequest.getUpStationId()).get();
        Station downStation = StationDao.findById(lineRequest.getDownStationId()).get();
        stations.add(upStation);
        stations.add(downStation);

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), stations);

        Line newLine = LineDao.save(line);

        List<StationResponse> stationResponses = newLine.getStations().stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stationResponses);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }
}
