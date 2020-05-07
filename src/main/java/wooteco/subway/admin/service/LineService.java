package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(Line line) {
        validate(line);
        return LineResponse.of(lineRepository.save(line));
    }

    private void validate(Line line) {
        List<Line> savedLines = lineRepository.findAll();
        for (Line savedLine : savedLines) {
            if (savedLine.getName().equals(line.getName())) {
                throw new IllegalArgumentException("중복되는 역이 존재합니다.");
            }
        }
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        return LineResponse.of(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        return LineResponse.of(lineRepository.save(persistLine));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        // TODO: 구현
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
    }

    public LineResponse findLineWithStationsById(Long id) {
        // TODO: 구현
        return new LineResponse();
    }
}
