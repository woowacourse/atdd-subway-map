package wooteco.subway.station.web;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationApiController {

    private final StationService stationService;

    @PostMapping
    public ResponseEntity createStation(
        @RequestBody @Valid StationRequest stationRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult);
        }
        final Station newStation =
            stationService.createStation(Station.create(stationRequest.getName()));

        return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
            .body(StationResponse.create(newStation));
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.findAll();

        List<StationResponse> stationResponses =
            stations.stream()
                .map(StationResponse::create)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
