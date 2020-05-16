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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.request.LineRequest;
import wooteco.subway.admin.dto.resopnse.ApiError;
import wooteco.subway.admin.dto.resopnse.LineResponse;
import wooteco.subway.admin.exception.DuplicateNameException;
import wooteco.subway.admin.exception.NotFoundException;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> findLines() {
        List<LineResponse> lines = lineService.findAllWithoutStations();
        return ResponseEntity.ok(lines);
    }

    @PostMapping("/lines")
    public ResponseEntity<Long> createLines(@RequestBody LineRequest lineRequest) {
        Long id = lineService.save(lineRequest.toLine());
        return ResponseEntity.created(URI.create("/lines/" + id)).body(id);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineWithoutStations(id);
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
        @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.deleteLineById(id);
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
