package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.line.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.line.LineUpdateRequestDto;
import wooteco.subway.controller.dto.response.line.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.line.LineResponseDto;
import wooteco.subway.controller.dto.response.line.LineStationsListResponseDto;
import wooteco.subway.service.line.LineService;
import wooteco.subway.service.line.LineStationsListService;

@RequestMapping("/lines")
@RestController
public class LineController {
    private final LineService lineService;
    private final LineStationsListService lineStationsListService;

    public LineController(LineService lineService, LineStationsListService lineStationsListService) {
        this.lineService = lineService;
        this.lineStationsListService = lineStationsListService;
    }

    @PostMapping(value = "",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineCreateResponseDto> createLine(@Valid @RequestBody LineCreateRequestDto lineCreateRequestDto) {
        LineCreateResponseDto lineCreateResponseDto = lineService.createLine(lineCreateRequestDto);
        return ResponseEntity
            .created(URI.create("/lines/" + lineCreateResponseDto.getId()))
            .body(lineCreateResponseDto);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponseDto>> getAllLines() {
        List<LineResponseDto> allLineResponses = lineService.getAllLines();
        return ResponseEntity.ok()
            .body(allLineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineStationsListResponseDto> getAllStationsInOrderListByLineId(@PathVariable Long id) {
        LineStationsListResponseDto lineStationsListResponseDto = lineStationsListService.getAllStationsInOrderListByLineId(id);
        return ResponseEntity.ok()
            .body(lineStationsListResponseDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @Valid @RequestBody LineUpdateRequestDto lineUpdateRequestDto) {
        lineService.updateLine(id, lineUpdateRequestDto);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLinById(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
