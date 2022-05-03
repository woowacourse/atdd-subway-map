package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequestDto;
import wooteco.subway.dto.response.StationResponseDto;

import java.net.URI;
import wooteco.subway.service.StationService;

@RestController
public class StationController {

    private final StationService stationService = new StationService(new StationDao());

    @PostMapping("/stations")
    public ResponseEntity<StationResponseDto> createStation(@RequestBody final StationRequestDto stationRequestDto) {
        final Station newStation = stationService.register(stationRequestDto.getName());
        final StationResponseDto stationResponseDto = new StationResponseDto(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponseDto);
    }

//    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<StationResponseDto>> showStations() {
//        List<Station> stations = StationDao.findAll();
//        List<StationResponseDto> stationResponsDtos = stations.stream()
//                .map(it -> new StationResponseDto(it.getId(), it.getName()))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok().body(stationResponsDtos);
//    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
