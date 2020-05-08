package wooteco.subway.admin.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.DuplicateLineException;
import wooteco.subway.admin.exception.LineNotFoundException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        if (isDistinct(line.getName())) {
            return lineRepository.save(line);
        }
        throw new DuplicateLineException(line.getName());
    }

    private boolean isDistinct(String name) {
        return lineRepository.countDistinctByName(name) == 0;
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        persistLine.update(line);
        return lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        lineRepository.deleteById(persistLine.getId());
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        persistLine.addLineStation(request.toLineStation());
        lineRepository.save(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
    }

    public LineResponse findLineWithStationsById(Long id) {
        // TODO: 구현
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        Set<Station> stations = stationRepository.findAllById(line.getStations()
            .stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList())
        );
        return LineResponse.of(line, stations);
    }
}
