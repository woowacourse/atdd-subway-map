package wooteco.subway.admin.controller;

import static java.util.stream.Collectors.*;

import java.net.URI;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;

@RestController
public class MockLineController {

    private Set<Station> stations = new LinkedHashSet<>();

    @PostMapping("/lines/{id}/stations")
    public ResponseEntity<LineResponse> appendStationToLine(@PathVariable Long id,
        @RequestBody StationCreateRequest request) {
        Line line = new Line(id, "8호선", LocalTime.of(5, 40), LocalTime.of(23, 57), 8, "bg-pink-500",
            new HashSet<>());

        stations.add(new Station("암사역"));
        stations.add(new Station("천호역"));
        stations.add(new Station("몽촌토성역"));
        stations.add(new Station("잠실역"));
        stations.add(request.toStation());

        return ResponseEntity.created(URI.create("/lines/" + id + "/stations"))
            .body(LineResponse.of(line, stations));
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity<List<StationResponse>> getStations(@PathVariable Long id) {
        List<StationResponse> stationResponses = stations.stream()
            .map(StationResponse::of)
            .collect(toList());
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity excludeStationFromLine(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        stations.removeIf(station -> station.getName().equals("석촌역"));
        return ResponseEntity.noContent().build();
    }
}
