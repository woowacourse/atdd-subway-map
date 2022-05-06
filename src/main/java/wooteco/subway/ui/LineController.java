package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Station downStation = stationDao.findById(lineRequest.getDownStationId());
        Station upStation = stationDao.findById(lineRequest.getUpStationId());

        Line line = new Line(lineRequest.getName(), lineRequest.getColor());

        lineDao.checkDuplication(line);
        Line newLine = lineDao.save(line);

        Section section = new Section(newLine.getId(), upStation.getId(), downStation.getId(), lineRequest.getDistance());
        sectionDao.save(section);

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), createStationResponse(newLine));
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponse(line)))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    private List<StationResponse> createStationResponse(Line line) {
        List<Section> sections = sectionDao.findSectionIn(line);

        Set<Long> stationIds = new HashSet<>();

        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        List<Station> stations = stationDao.findByIdIn(stationIds);

        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> searchLine(@PathVariable Long id) {

        Line line = lineDao.findById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponse(line));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineDao.edit(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> lineNotFound() {
        return ResponseEntity.noContent().build();
    }
}
