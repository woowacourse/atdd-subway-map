package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.dto.StationSaveRequest;
import wooteco.subway.service.StationService;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody @Valid StationSaveRequest stationSaveRequest) {
        StationResponse response = stationService.save(stationSaveRequest.toStation());
        return ResponseEntity.created(URI.create("/stations/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAll());
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<Void> deleteStation(
            @PathVariable @Positive(message = "노선의 id는 양수 값만 들어올 수 있습니다.") Long stationId) {
        stationService.delete(stationId);
        return ResponseEntity.noContent().build();
    }
}
