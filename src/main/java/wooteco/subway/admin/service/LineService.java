package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.LineRequest;
import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String DUPLICATE_LINE_NAME_ERR_MSG = "중복된 노선 이름은 허용되지 않습니다.";
    private static final String NOT_EXIST_ID_ERR_MSG = "존재하지 않는 id입니다.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(Line line) {
        try {
            return LineResponse.of(lineRepository.save(line));
        } catch (Exception e) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_ERR_MSG);
        }
    }

    public List<LineResponse> getLineResponses() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, findStations(line)))
                .collect(Collectors.toList());
    }

    private List<Station> findStations(Line line) {
        return stationRepository.findAllByLineId(line.getId());
    }

    public LineResponse getLineResponse(Long id) {
        Line line = findById(id);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        checkSameNameLinePresent(lineRequest);

        Line line = findById(id);
        line.update(lineRequest.toLine());
        lineRepository.save(line);
        List<Station> stations = findStations(line);

        return LineResponse.of(line, stations);
    }

    private void checkSameNameLinePresent(LineRequest lineRequest) {
        List<Line> allLines = lineRepository.findAll();
        Optional<Line> sameNameLine = allLines.stream()
                .filter(line -> line.getName().equals(lineRequest.getName()))
                .findAny();

        if (sameNameLine.isPresent()) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_ERR_MSG);
        }
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findById(id);
        line.addLineStation(request.toEntity());
        lineRepository.save(line);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_ID_ERR_MSG));
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long stationId) {
        Line line = findById(stationId);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }
}