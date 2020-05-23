package wooteco.subway.admin.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.exception.CommonException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(()-> new CommonException(HttpStatus.BAD_REQUEST, "Not exist line id = " + id));
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Line addLineStation(Long id, LineStation lineStation) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new CommonException(HttpStatus.BAD_REQUEST, "Not exist line id = " + id));
        line.addLineStation(lineStation);
        return lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(()-> new CommonException(HttpStatus.BAD_REQUEST, "Not exist line id = " + lineId));
        line.removeLineStationByStationId(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new CommonException(HttpStatus.BAD_REQUEST, "Not exist line id = " + id));
        return LineResponse.of(line);
    }

    public LineWithStationsResponse findLineWithStationsById(Long id) {
        return findLineWithStations(lineRepository.findById(id)
                        .orElseThrow(()-> new CommonException(HttpStatus.BAD_REQUEST, "Not exist line id = " + id)));
    }

    public List<LineWithStationsResponse> showLinesWithStations() {
        return Collections.unmodifiableList(
                lineRepository.findAll()
                        .stream()
                        .map(this::findLineWithStations)
                        .collect(Collectors.toList()));
    }

    private LineWithStationsResponse findLineWithStations(Line line) {
        List<Station> orderedStations = stationRepository.findByIds(line.getStationIds());
        return LineWithStationsResponse.of(line, orderedStations);
    }
}
