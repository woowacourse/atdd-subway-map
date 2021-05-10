package wooteco.subway.web.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
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
import wooteco.subway.service.StationService;
import wooteco.subway.web.dto.StationRequest;
import wooteco.subway.web.dto.StationResponse;

@RestController
@RequestMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> create(
            @RequestBody @Valid StationRequest stationRequest) {
        Station station = stationService.add(stationRequest.toEntity());

        return ResponseEntity
                .created(URI.create("/stations/" + station.getId()))
                .body(new StationResponse(station));
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> list() {
        List<StationResponse> stationResponses = stationService.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stationService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
