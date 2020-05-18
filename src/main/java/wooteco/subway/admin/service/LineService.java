package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String NOT_FOUND_LINE = "해당되는 노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse addLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getStartTime(),
                lineRequest.getEndTime(), lineRequest.getIntervalTime(), lineRequest.getBgColor());
        validateDuplicate(line);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private void validateDuplicate(Line line) {
        if (lineRepository.findByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                .stream()
                .map(this::findLineWithStationsByLine)
                .collect(Collectors.toList());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        Line line = new Line(lineRequest.getName(), lineRequest.getStartTime(),
                lineRequest.getEndTime(), lineRequest.getIntervalTime(), lineRequest.getBgColor());
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_LINE));
        line.addLineStation(request.toLineStation());
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_LINE));
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_LINE));
        return findLineWithStationsByLine(line);
    }

    private LineResponse findLineWithStationsByLine(Line line) {
        return LineResponse.of(line, line.createSortedStations(stationRepository.findAllById(line.getLineStationsId())));
    }
}
