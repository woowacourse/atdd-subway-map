package wooteco.subway.station.controller;

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
import wooteco.subway.station.service.dto.StationCreateDto;
import wooteco.subway.station.service.dto.StationDto;
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
    public ResponseEntity<StationResponse> createStation(@Valid @RequestBody final StationRequest stationRequest) {
        final StationCreateDto stationInfo = stationRequest.toStationCreateDto();

        final StationDto createdStationInfo = stationService.save(stationInfo);

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
