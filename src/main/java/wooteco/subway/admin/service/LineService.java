package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.domain.service.LineStationService;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final LineStationService lineStationService;

    public LineService(final LineRepository lineRepository, final LineStationService lineStationService) {
        this.lineRepository = lineRepository;
        this.lineStationService = lineStationService;
    }

    @Transactional
    public Line save(Line line) {
        return lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLineResponses() {
        List<Line> lines = lineRepository.findAll();

        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            Set<Station> stations = line.convertStations(lineStationService);
            LineResponse lineResponse = LineResponse.of(line, stations);
            lineResponses.add(lineResponse);
        }

        return lineResponses;
    }

    @Transactional
    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addLineStation(Long id, LineStationCreateRequest request) {
        // TODO: 구현
    }

    @Transactional
    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(line, line.convertStations(lineStationService));
    }
}
