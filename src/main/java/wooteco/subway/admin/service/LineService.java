package wooteco.subway.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    @Autowired
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

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        LineStation lineStation = new LineStation(request.getLine(), request.getPreStationId(), request.getStationId(),
                request.getDistance(), request.getDuration());
        line.addLineStation(lineStation);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);

        line.removeLineStationById(stationId);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        LineResponse lineResponse = LineResponse.of(line);
        Set<Station> stations = (Set<Station>) stationRepository.findAllById(line.getLineStationsId());

        lineResponse.updateLineStations(stations);
        return lineResponse;
    }
}
