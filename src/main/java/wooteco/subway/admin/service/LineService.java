package wooteco.subway.admin.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.LineRequest;
import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.exceptions.DuplicateLineNameException;
import wooteco.subway.admin.exceptions.LineNotFoundException;
import wooteco.subway.admin.exceptions.StationNotFoundException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        if (lineRepository.hasLineName(lineRequest.getName()).isPresent()) {
            throw new DuplicateLineNameException(lineRequest.getName());
        }
        Line line = lineRequest.toLine();
        save(line);
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        Set<Long> stationIds = getMatchingStationIds(lines);
        List<Station> stations = stationRepository.findAllById(stationIds);

        return matchLineWithStations(lines, stations);
    }

    private Set<Long> getMatchingStationIds(List<Line> lines) {
        return lines.stream()
                .map(Line::getLineStationsId)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<LineResponse> matchLineWithStations(List<Line> lines, List<Station> stations) {
        return lines.stream()
                .map(line -> LineResponse.of(line, line.findMatchingStations(stations)))
                .collect(Collectors.toList());
    }

    private List<Station> findStations(Line line) {
        return stationRepository.findAllByLineId(line.getId());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = findById(id);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(lineRequest.toLine());
        save(line);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    private void checkDuplicateKeyException(DbActionExecutionException e, String name) {
        if (e.getCause() instanceof DuplicateKeyException) {
            throw new DuplicateLineNameException(name);
        }
    }

    private void save(Line line) {
        try {
            lineRepository.save(line);
        } catch (DbActionExecutionException e) {
            checkDuplicateKeyException(e, line.getName());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationCreateRequest request) {
        LineStation lineStation = request.toLineStation();
        validateStationExist(lineStation);

        Line line = findById(id);
        line.addLineStation(lineStation);
        lineRepository.save(line);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    private void validateStationExist(LineStation lineStation) {
        if (lineStation.isStationNotExist() || !stationRepository.findById(lineStation.getStationId()).isPresent()) {
            throw new StationNotFoundException(lineStation.getStationId());
        }
        if (lineStation.isPreStationExist() && !stationRepository.findById(lineStation.getPreStationId()).isPresent()) {
            throw new StationNotFoundException(lineStation.getPreStationId());
        }
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(id));
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long stationId) {
        Line line = findById(stationId);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }
}