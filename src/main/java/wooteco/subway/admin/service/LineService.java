package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.exception.DuplicatedLineException;
import wooteco.subway.admin.exception.NotFoundLineException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        checkExistLine(line);
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(NotFoundLineException::new);
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = findLineById(id);
        if (!Objects.equals(persistLine.getName(), line.getName())) {
            checkExistLine(line);
        }
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private void checkExistLine(Line line) {
        if (lineRepository.existsByName(line.getName())) {
            throw new DuplicatedLineException(line.getName());
        }
    }

    public void addLineStation(Long id, LineStation lineStation) {
        Line persistLine = findLineById(id);
        persistLine.addLineStation(lineStation);
        lineRepository.save(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = findLineById(lineId);
        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = findLineById(id);
        Set<Station> stations = findStationsByLine(line);
        return LineResponse.of(line, stations);
    }

    public List<LineResponse> findAllLineWithStations() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, findStationsByLine(line)))
                .collect(Collectors.toList());
    }

    private Set<Station> findStationsByLine(Line line) {
        List<Long> ids = line.getLineStationsId();
        Map<Long, Station> stations = stationRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Station::getId, station -> station));
        return ids.stream()
                .map(stations::get)
                .collect(Collectors.collectingAndThen(Collectors.toList(), LinkedHashSet::new));
    }
}
