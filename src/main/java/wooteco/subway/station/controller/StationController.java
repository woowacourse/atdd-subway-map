package wooteco.subway.station.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.controller.dto.StationDto;
import wooteco.subway.station.controller.dto.StationRequest;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.service.StationService;

@RestController
@RequestMapping("/stations")
public final class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody final StationRequest stationRequest) {
        final Station requestedStation = new Station(stationRequest);

        final StationDto createdStationInfo = stationService.save(requestedStation);

        final StationResponse stationResponse = StationResponse.of(createdStationInfo);
        final Long stationId = stationResponse.getId();
        return ResponseEntity.created(URI.create("/stations/" + stationId)).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        final List<StationDto> stationsInfo = stationService.showAll();

        final List<StationResponse> stationResponses = stationsInfo.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable final Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
