package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Long save(Line line) {
        lineRepository.findByName(line.getName())
                .ifPresent(this::throwAlreadySavedException);

        Line persistLine = lineRepository.save(line);
        return persistLine.getId();
    }

    private void throwAlreadySavedException(Line line) {
        throw new IllegalArgumentException(line.getName() + " : 이미 존재하는 노선 이름입니다.");
    }

    @Transactional(readOnly = true)
    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLineResponses() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line, stationRepository.findAllById(line.getLineStationsId())))
                .collect(Collectors.toList());
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
    public void addLineStation(Long lineId, EdgeCreateRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(lineId + " : 존재하지 않는 노선값 입니다."));
        line.addLineStation(request.toEdge());
    }

    @Transactional
    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(lineId + " : 존재하지 않는 노선값 입니다."));
        line.removeLineStationById(stationId);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(line, stationRepository.findAllById(line.getLineStationsId()));
    }
}
