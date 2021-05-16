package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.StationRequest;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.service.StationService;
import wooteco.subway.service.dto.StationServiceDto;

@RestController
@RequestMapping(value = "/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(
        @Valid @RequestBody StationRequest stationRequest) {

        StationServiceDto stationServiceDto = new StationServiceDto(stationRequest.getName());
        StationServiceDto savedStationServiceDto = stationService.save(stationServiceDto);
        StationResponse stationResponse = StationResponse.from(savedStationServiceDto);

        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId()))
            .body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationServiceDto> stationServiceDtos = stationService.showStations();
        List<StationResponse> stationResponses = stationServiceDtos.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());

        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@NotNull @PathVariable Long id) {
        stationService.delete(new StationServiceDto(id));

        return ResponseEntity.noContent()
            .build();
    }
}
