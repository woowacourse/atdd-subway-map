package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<LineResponse> showLines() {
        return LineResponse.listOf(lineRepository.findAll());
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.addLineStation(request.toLineStation());
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.removeLineStationByStationId(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(line);
    }

    public LineWithStationsResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return findLineWithStations(line);
    }

    public List<LineWithStationsResponse> showLinesWithStations() {
        List<Line> lines = lineRepository.findAll();
        return Collections.unmodifiableList(
                lines.stream()
                    .map(this::findLineWithStations)
                    .collect(Collectors.toList()));
    }

    private LineWithStationsResponse findLineWithStations(Line line) {
        List<Long> orderedStationIds = line.getStationsId();
        List<Station> orderedStations = stationRepository.findByIds(orderedStationIds);
        return LineWithStationsResponse.of(line, orderedStations);
    }
}
