package wooteco.subway.admin.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.DuplicateLineException;
import wooteco.subway.admin.exception.LineNotFoundException;
import wooteco.subway.admin.exception.StationNotFoundException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        try {
            validateUniquenessOf(line.getName());
            return lineRepository.save(line);
        } catch (DbActionExecutionException e) {
            throw new DuplicateLineException(line.getName());
        }
    }

    private void validateUniquenessOf(String name) {
        if (lineRepository.countDistinctByName(name) != 0) {
            throw new DuplicateLineException(name);
        }
    }

    public List<LineResponse> findAllLinesWithStationsById() {
        return lineRepository.findAll()
            .stream()
            .map(line -> findLineWithStationsById(line.getId()))
            .collect(Collectors.toList());
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        List<Station> stations = stationRepository.findAllByLineId(line.getId());
        return LineResponse.of(line, stations);
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        validateExistenceOf(request.getStationId());
        persistLine.addLineStation(request.toLineStation());
        lineRepository.save(persistLine);
    }

    private void validateExistenceOf(final Long stationId) {
        if (Objects.isNull(stationId)
            || !stationRepository.findById(stationId).isPresent()) {
            throw new StationNotFoundException(stationId);
        }
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = lineRepository.findById(lineId)
            .orElseThrow(() -> new LineNotFoundException(lineId));
        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }
}
