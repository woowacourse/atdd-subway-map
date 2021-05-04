package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.StationRequestDto;
import wooteco.subway.controller.dto.response.StationResponseDto;
import wooteco.subway.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponseDto> createStation(@RequestBody StationRequestDto stationRequestDto) {
        StationResponseDto stationResponseDto = stationService.createStation(stationRequestDto);
        return ResponseEntity
            .created(URI.create("/stations/" + stationResponseDto.getId()))
            .body(stationResponseDto);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponseDto>> showAllStations() {
        List<StationResponseDto> allStationResponses = stationService.getAllStations();
        return ResponseEntity.ok()
            .body(allStationResponses);
    }

    @DeleteMapping(value = "/stations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteStationById(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
