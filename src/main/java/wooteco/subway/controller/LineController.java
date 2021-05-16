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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.request.SectionRequest;
import wooteco.subway.controller.dto.request.UpdateLineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.dto.CreateLineDto;
import wooteco.subway.service.dto.CreateSectionDto;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.service.dto.ReadLineDto;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody LineRequest lineRequest) {
        CreateLineDto createLineDto = CreateLineDto.from(lineRequest);
        LineServiceDto createdLineServiceDto = lineService.createLine(createLineDto);
        LineResponse lineResponse = LineResponse.from(createdLineServiceDto);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
            .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineServiceDto> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@NotNull @PathVariable Long id) {
        ReadLineDto readLineDto = lineService.findOne(new LineServiceDto(id));
        LineResponse lineResponse = LineResponse.from(readLineDto);
        System.out.println(lineResponse.getName());
        System.out.println(lineResponse.getStations());
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(
        @Valid @RequestBody UpdateLineRequest updateLineRequest, @NotNull @PathVariable Long id) {

        LineServiceDto lineServiceDto = LineServiceDto.from(id, updateLineRequest);
        lineService.update(lineServiceDto);

        return ResponseEntity.ok()
            .build();
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity<Void> createSection(@Valid @RequestBody SectionRequest sectionRequest,
        @NotNull @PathVariable Long lineId) {
        CreateSectionDto createSectionDto = CreateSectionDto.of(lineId, sectionRequest);
        lineService.createSection(createSectionDto);

        return ResponseEntity.ok()
            .build();
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity<Void> deleteStationOnSection(@NotNull @PathVariable Long lineId,
        @NotNull @RequestParam Long stationId) {
        lineService.deleteStation(lineId, stationId);

        return ResponseEntity.noContent()
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@NotNull @PathVariable Long id) {
        lineService.delete(new LineServiceDto(id));

        return ResponseEntity.noContent()
            .build();
    }
}
