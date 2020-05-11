package wooteco.subway.admin.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.LineStationRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private LineRepository lineRepository;
    private LineStationRepository lineStationRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
        LineStationRepository lineStationRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    private List<Line> showLines() {
        return lineRepository.findAll();
    }

    public List<LineResponse> findAllLine() {
        return LineResponse.listOf(showLines(), mappingLineStation());
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
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존지하지 않습니다"));
        LineStation InputLineStation = new LineStation(
            request.getPreStationId(),
            request.getStationId(),
            request.getDistance(),
            request.getDuration()
        );

        line.addLineStation(InputLineStation);
        lineRepository.save(line);
    }

    public List<LineStation> getLineStations(Long id) {
        return lineStationRepository.findAllByLine(id);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다"));

        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 라인아이디가 존지하지 않습니다"));
        return LineResponse.of(line, generateStations(line));
    }

    private Set<Station> generateStations(Line line) {
        if (line.isStationsEmpty()) {
            return new HashSet<>();
        }
        List<Long> stationIds = line.generateLineStationId();
        return stationRepository.findAllById(stationIds);
    }

    private Map<Long, Set<Station>> mappingLineStation() {
        Map<Long, Set<Station>> mappingLineStation = new HashMap<>();
        List<Line> lines = showLines();
        for (Line line : lines) {
            mappingLineStation.put(line.getId(), generateStations(line));
        }
        return mappingLineStation;
    }

    public void validateTitle(LineRequest lineRequest) {
        lineRepository.findByTitle(lineRequest.getTitle()).ifPresent(line -> {
            throw new IllegalArgumentException("존재하는 이름입니다");
        });
    }

    public void validateTitleWhenUpdate(Long id, LineRequest lineRequest) {
        Line lineById = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        if (lineById.getTitle().equals(lineRequest.getTitle())) {
            return;
        }
        validateTitle(lineRequest);
    }
}