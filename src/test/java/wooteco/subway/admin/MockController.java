package wooteco.subway.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationCreateRequest;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/lines")
public class MockController {
    private static Map<Long, Line> lines = new HashMap<>();
    private static Map<Long, Station> stations = new HashMap<>();
    private static Map<Long, List<LineStation>> lineStations = new HashMap<>();

    static {
        lines.put(1L, new Line("name", LocalTime.of(5, 30), LocalTime.of(23, 30), 10, "bg-blue-200"));
        stations.put(1L, new Station("강남역"));
        stations.put(2L, new Station("역삼역"));
        lineStations.put(1L, new ArrayList<>());
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity addStationToLine(@PathVariable Long id, @RequestBody LineStationCreateRequest request) {
        LineStation lineStation = new LineStation(request.getPreStationId(), request.getStationId(), request.getDistance(), request.getDuration());
        lineStations.get(id).add(lineStation);
        return ResponseEntity.created(URI.create("")).build();
    }
}
