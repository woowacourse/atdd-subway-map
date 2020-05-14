package wooteco.subway.admin.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.AlreadyExistNameException;
import wooteco.subway.admin.exception.NotExistIdException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        validateDuplicateName(line);
        return lineRepository.save(line);

    }

    private void validateDuplicateName(Line line) {
        Line newLine = lineRepository.findByName(line.getName());

        if (Objects.nonNull(newLine) && line.getStations().size() == 0) {
            throw new AlreadyExistNameException(line.getName());
        }
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    @Transactional
    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(() -> new NotExistIdException(id));
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(RuntimeException::new);
    }

    public Line addLineStation(Long id, LineStationCreateRequest request) {
        LineStation lineStation = request.toLineStation();
        Line line = findById(id);
        line.addLineStation(lineStation);
        return save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        save(line);
    }

    public List<Station> findStationsByLineId(final List<Long> lineStationsIds) {
        return stationRepository.findAllById(lineStationsIds);
    }
}
