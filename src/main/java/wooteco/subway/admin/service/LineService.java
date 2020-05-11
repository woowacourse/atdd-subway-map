package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_DUPLICATE_LINE_NAME = "노선 이름이 중복됩니다.";
    private static final String ERROR_MESSAGE_WRONG_LINE_ID = "잘못된 라인 아이디를 입력하였습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Line save(Line line) {
        validateNameDuplicate(line);
        return lineRepository.save(line);
    }

    public List<LineResponse> showLines() {
        List<LineResponse> lineResponses = new ArrayList<>();
        List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            Set<StationResponse> stationResponses = getStationResponses(line);
            lineResponses.add(LineResponse.of(line, stationResponses));
        }

        return Collections.unmodifiableList(lineResponses);
    }

    private Set<StationResponse> getStationResponses(Line line) {
        List<Long> lineStationsId = line.findLineStationsId();
        Set<Station> stations = stationRepository.findAllById(lineStationsId);
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
    }

    @Transactional
    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    private void validateNameDuplicate(Line line) {
        if (lineRepository.existsByName(line.getName())) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_LINE_NAME);
        }
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ERROR_MESSAGE_WRONG_LINE_ID));
        line.addLineStation(request.toLineStationRequest());
        lineRepository.save(line);
    }

    @Transactional
    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() ->
                new IllegalArgumentException(ERROR_MESSAGE_WRONG_LINE_ID));
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        Set<StationResponse> stationResponses = getStationResponses(line);

        return LineResponse.of(line, stationResponses);
    }

    public List<LineStationResponse> findLineStations(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException(ERROR_MESSAGE_WRONG_LINE_ID));
        List<LineStation> lineStations = line.getLineStations();

        return lineStations.stream()
                .map(lineStation -> LineStationResponse.of(id, lineStation))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }
}
