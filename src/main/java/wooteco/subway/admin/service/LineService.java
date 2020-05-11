package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
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
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        return lineRepository.save(persistLine);
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

    public LineResponse findLineWithStationsById(Long id) {
        Line line = findById(id);
        List<Long> lineStationsIds = line.getLineStationsId();
        List<Station> stations = stationRepository.findAllById(lineStationsIds);

        return new LineResponse(line.getId(), line.getName(), line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getBackgroundColor(), line.getCreatedAt(), line.getUpdatedAt(), stations);
    }

    public List<Station> findStationsByLineId(final List<Long> lineStationsIds) {
        return stationRepository.findAllById(lineStationsIds);
    }
}
