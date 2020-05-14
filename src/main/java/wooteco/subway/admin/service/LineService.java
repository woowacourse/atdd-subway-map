package wooteco.subway.admin.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
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
        if (existsByName(line.getName())) {
            throw new DuplicateKeyException("중복되는 값을 생성하실 수 없습니다.");
        }
        return lineRepository.save(line);
    }

    public boolean existsByName(String name) {
        return lineRepository.existsByName(name);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line showLine(Long id) {
        return lineRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest lineStationCreateRequest) {
        Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        LineStation lineStation = lineStationCreateRequest.toLineStation();
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

     public LineResponse findLineWithStationsById(Long id) {
         Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
         Set<Station> stations = toStations(line.findLineStationsId());
         return LineResponse.of(line, stations);
     }

    public Set<Station> toStations(List<Long> lineStationsId) {
        return new LinkedHashSet<>(stationRepository.findAllById(lineStationsId));
    }
}
