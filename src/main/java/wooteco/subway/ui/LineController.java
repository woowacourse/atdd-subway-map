package wooteco.subway.ui;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.application.line.LineService;
import wooteco.subway.application.station.StationService;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.LineColor;
import wooteco.subway.domain.line.value.LineId;
import wooteco.subway.domain.line.value.LineName;
import wooteco.subway.domain.station.Station;
import wooteco.subway.ui.dto.SectionRequest;
import wooteco.subway.ui.dto.line.LineRequest;
import wooteco.subway.ui.dto.line.LineResponse;
import wooteco.subway.ui.dto.station.StationResponse;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineService lineService;
    private final StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    //todo Do not use LineRequest, just Entity.
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createNewLine(@RequestBody LineRequest lineRequest) {
        final Section section = new Section(
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
        );

        final Sections sections = new Sections(Collections.singletonList(section));

        final Line line = new Line(
                new LineName(lineRequest.getName()),
                new LineColor(lineRequest.getColor()),
                sections
        );

        final Line savedLine = lineService.save(line);
        List<StationResponse> stationResponses = getStationResponses(savedLine);


        return ResponseEntity
                .created(URI.create("/lines/" + savedLine.getLineId()))
                .body(
                        new LineResponse(
                                savedLine.getLineId(),
                                savedLine.getLineName(),
                                savedLine.getLineColor(),
                                stationResponses
                        )
                );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> allLines() {
        final List<LineResponse> lineResponses = lineService.allLines().stream()
                .map(line ->
                        new LineResponse(
                                line.getLineId(),
                                line.getLineName(),
                                line.getLineColor(),
                                getStationResponses(line)
                        )
                ).collect(toList());

        return ResponseEntity.ok(lineResponses);
    }

    private List<StationResponse> getStationResponses(Line line) {
        return line.getStationIds().stream()
                .map(stationService::findById)
                .map(StationResponse::new)
                .collect(toList());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        final Line line = lineService.findById(id);

        return ResponseEntity.ok(
                new LineResponse(
                        line.getLineId(),
                        line.getLineName(),
                        line.getLineColor(),
                        getStationResponses(line)
                )
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyById(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
<<<<<<< HEAD
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
=======
        final Line line = new Line(
                new LineId(id),
                new LineName(lineRequest.getName()),
                new LineColor(lineRequest.getColor())
        );
>>>>>>> d2a85ea... refactor: 테스트 및 버그 수정

        lineService.update(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{lineId}/sections")
    public ResponseEntity<Void> deleteSectionByStationId(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        Line line = lineService.findById(lineId);
        line.deleteSectionByStationId(stationId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity<LineResponse> addNewSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());

        lineService.addNewSection(lineId, section);
        Line line = lineService.findById(lineId);

        List<Station> stations = line.getStationIds().stream()
                .map(stationService::findById)
                .collect(toList());

        List<StationResponse> stationResponses = stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(toList());

        return ResponseEntity.ok(
                new LineResponse(
                        line.getLineId(),
                        line.getLineName(),
                        line.getLineColor(),
                        stationResponses
                )
        );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> duplicationKeyExceptionHandle(Exception e) {
        System.out.println(e.getMessage());
        return ResponseEntity.badRequest().body("동일한 라인을 등록할 수 없습니다");
    }

    @ExceptionHandler(DataAccessException.class)
    private ResponseEntity<String> handleDatabaseExceptions(Exception e) {
        System.out.println(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandle(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
