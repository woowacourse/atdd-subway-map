package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.assembler.Assembler;
import wooteco.subway.exception.DuplicatedStationNameException;
import wooteco.subway.exception.VoidStationException;
import wooteco.subway.station.dto.StationDto;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController() {
        Assembler assembler = new Assembler();
        this.stationService = assembler.getStationService();
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        try {
            StationDto stationDto = new StationDto(stationRequest.getName());
            StationDto savedStationDto = stationService.save(stationDto);
            StationResponse stationResponse = new StationResponse(savedStationDto.getId(),
                savedStationDto.getName());
            return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId()))
                .body(stationResponse);
        } catch (DuplicatedStationNameException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationDto> stationDtos = stationService.showStations();
        List<StationResponse> stationResponses = stationDtos.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        try {
            stationService.delete(new StationDto(id));
        } catch (VoidStationException e) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }
}
