package wooteco.subway.admin.service;

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
import java.util.Optional;
import java.util.Set;
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
        Optional<Line> duplicatedLine = lineRepository.findByName(line.getName());
        if (duplicatedLine.isPresent()) {
            throw new IllegalArgumentException("중복된 노선 이름은 등록할 수 없습니다.");
        }
        return lineRepository.save(line);
    }

    public LineResponse showLine(Long id) {
        Line line = findLineBy(id);
        return LineResponse.of(line);
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> findLineWithStationsById(line.getId()))
                .collect(Collectors.toList());
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = findLineBy(id);
        persistLine.update(line);
        lineRepository.save(persistLine);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findLineBy(id);
        LineStation lineStation = new LineStation(request.getPreStationId(), request.getStationId(),
                request.getDistance(), request.getDuration());
        line.addLineStation(lineStation);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine, findStations(persistLine));
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findLineBy(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = findLineBy(id);
        Set<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    private Line findLineBy(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("노선이 존재하지 않습니다."));
    }

    private Set<Station> findStations(Line line) {
        return stationRepository.findAllById(line.findLineStationsId());
    }
}
