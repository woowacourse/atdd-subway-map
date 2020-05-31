package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.error.NotFoundException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

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

    public List<Line> getLines() {
        return lineRepository.findAll();
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findById(id);
        LineStation lineStation = new LineStation(request.getPreStationId(), request.getStationId(), request.getDistance(), request.getDuration());
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException("id에 해당하는 Line을 찾을 수 없습니다"));
    }

    public void delete(Line line) {
        lineRepository.delete(line);
    }

    public Line findByName(String name) {
        return lineRepository.findByName(name);
    }
}
