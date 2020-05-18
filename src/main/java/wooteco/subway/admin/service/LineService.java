package wooteco.subway.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationsResponse;
import wooteco.subway.admin.exception.EntityNotFoundException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private static final String LINE_NOT_FOUND_EXCEPTION = "노선을 찾을 수 없습니다.";
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(Line line) {
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(LINE_NOT_FOUND_EXCEPTION));
        persistLine.update(line);
        lineRepository.save(persistLine);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long lineId, LineStationCreateRequest lineStationCreateRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException(LINE_NOT_FOUND_EXCEPTION));
        line.addLineStation(lineStationCreateRequest.toLineStation());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException(LINE_NOT_FOUND_EXCEPTION));
        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }

    public LineResponse findLineById(Long id) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(LINE_NOT_FOUND_EXCEPTION));
        return LineResponse.of(persistLine);
    }

    public List<LineStationsResponse> findAllLineStations() {
        List<Line> lines = lineRepository.findAll();
        List<Long> lineIds = generateStationIds(lines);
        Map<Long, Station> stations = generateStationMapper(lineIds);
        return lines.stream()
                .map(line -> LineStationsResponse.of(line, stations))
                .collect(Collectors.toList());
    }

    private List<Long> generateStationIds(List<Line> lines) {
        return lines.stream()
                .flatMap(line -> line.getLineStationIds().stream())
                .collect(Collectors.toList());
    }

    public LineStationsResponse findLineStationsById(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException(LINE_NOT_FOUND_EXCEPTION));
        Map<Long, Station> stations = generateStationMapper(line.getLineStationIds());
        return LineStationsResponse.of(line, stations);
    }

    private Map<Long, Station> generateStationMapper(List<Long> stationIds) {
        return stationRepository.findAllById(stationIds)
                .stream()
                .collect(Collectors.toMap(
                        Station::getId,
                        station -> station));
    }
}
