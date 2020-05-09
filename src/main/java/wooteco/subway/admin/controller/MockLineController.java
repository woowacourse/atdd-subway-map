package wooteco.subway.admin.controller;

import static java.util.stream.Collectors.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;

@RestController
public class MockLineController {

    private Set<Station> stations = new LinkedHashSet<>();


    @GetMapping("/lines/{id}/stations")
    public ResponseEntity<List<StationResponse>> getStations(@PathVariable Long id) {
        List<StationResponse> stationResponses = stations.stream()
            .map(StationResponse::of)
            .collect(toList());
        return ResponseEntity.ok(stationResponses);
    }
}
