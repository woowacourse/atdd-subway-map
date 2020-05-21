package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.NotFoundLineIdException;
import wooteco.subway.admin.exception.NotFoundStationIdException;
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

    public Long save(Line line) {
        return lineRepository.save(line).getId();
    }

    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundLineIdException::new);
    }

    public List<Station> findStationsByLineId(Long id) {
        Line line = findLineById(id);
        return line.makeLineStationsIds()
            .stream()
            .map(stationRepository::findById)
            .map(optional -> optional.orElseThrow(NotFoundStationIdException::new))
            .collect(Collectors.toList());
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = findLineById(id);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findLineById(id);
        LineStation lineStation = request.toLineStation();
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }
}
