package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.domain.exception.DuplicationNameException;
import wooteco.subway.admin.domain.exception.NotFoundLineException;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.LineStationRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final LineStationRepository lineStationRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       LineStationRepository lineStationRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        validateTitle(line);
        return lineRepository.save(line);
    }

    private void validateTitle(Line line) {
        lineRepository.findByTitle(line.getTitle())
                .ifPresent(x -> {
                    throw new DuplicationNameException(line.getTitle());
                });
    }

    public void updateLine(Long id, Line line) {
        validateTitleWhenUpdateInfo(id, line);
        Line persistLine = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    private void validateTitleWhenUpdateInfo(Long id, Line line) {
        if (isNotChangeTitle(id, line)) {
            return;
        }
        validateTitle(line);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long lineId, LineStation lineStation) {
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);
        line.updatePreStationWhenAdd(lineStation);
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public List<LineStation> findLineStationByLineId(Long lineId) {
        return lineStationRepository.findAllByLineId(lineId);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        List<Station> stations = getStationsByLine(line);
        return LineResponse.of(line, new LinkedHashSet<>(stations));
    }

    private List<Station> getStationsByLine(final Line line) {
        return line.getLineStationsId().stream()
                .map(stationId -> stationRepository.findById(stationId).orElseThrow(NotFoundLineException::new))
                .collect(Collectors.toList());
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);
        line.updatePreStationWhenRemove(stationId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    private boolean isNotChangeTitle(final Long id, final Line line) {
        Line lineById = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        return lineById.isTitleEquals(line.getTitle());
    }
}
