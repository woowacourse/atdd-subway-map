package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.station.StationCreateRequestDto;
import wooteco.subway.controller.dto.response.station.StationResponseDto;
import wooteco.subway.service.StationService;

@RequestMapping("/stations")
@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping(value = "",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponseDto> createStation(@RequestBody StationCreateRequestDto stationCreateRequestDto) {
        StationResponseDto stationResponseDto = stationService.createStation(stationCreateRequestDto);
        return ResponseEntity
            .created(URI.create("/stations/" + stationResponseDto.getId()))
            .body(stationResponseDto);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponseDto>> showAllStations() {
        List<StationResponseDto> allStationResponses = stationService.getAllStations();
        return ResponseEntity.ok()
            .body(allStationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStationById(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
