package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.resopnse.ApiError;
import wooteco.subway.admin.dto.resopnse.StationResponse;
import wooteco.subway.admin.exception.DuplicateNameException;
import wooteco.subway.admin.exception.NotFoundException;
import wooteco.subway.admin.service.StationService;

@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stations = stationService.getAll();
        return ResponseEntity.ok().body(stations);
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<StationResponse> getStation(@PathVariable Long id) {
        StationResponse stationResponse = stationService.getById(id);
        return ResponseEntity.ok(stationResponse);
    }

    @PostMapping("/stations")
    public ResponseEntity<Long> createStation(@RequestBody StationCreateRequest view) {
        Long savedId = stationService.save(view);

        return ResponseEntity
            .created(URI.create("/stations/" + savedId))
            .body(savedId);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError notFoundException(NotFoundException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DuplicateNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError duplicateNameException(DuplicateNameException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
