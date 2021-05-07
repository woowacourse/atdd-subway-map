package wooteco.subway.station;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.station.dto.StationDto;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(
        @RequestBody final StationRequest stationRequest) {

        StationDto stationDto = new StationDto(stationRequest.getName());
        StationDto savedStationDto = stationService.save(stationDto);
        StationResponse stationResponse = new StationResponse(
            savedStationDto.getId(),
            savedStationDto.getName()
        );

        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId()))
            .body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationDto> stationDtos = stationService.showStations();
        List<StationResponse> stationResponses = stationDtos.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());

        return ResponseEntity.ok()
            .body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable final Long id) {
        stationService.delete(new StationDto(id));

        return ResponseEntity.noContent()
            .build();
    }
}
