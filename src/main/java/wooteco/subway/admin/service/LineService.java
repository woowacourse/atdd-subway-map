package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Edges;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.EdgeCreateRequest;
import wooteco.subway.admin.dto.request.LineRequest;
import wooteco.subway.admin.dto.request.LineUpdateRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.StationsAtLineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String DUPLICATED_STATION_EXCEPTION_MESSAGE = "이미 동일한 역이 존재합니다.";
    private static final String NO_SUCH_LINE_EXCEPTION_MESSAGE = "존재하지 않는 노선입니다.";
    private static final String NO_SUCH_EDGE_EXCEPTION_MESSAGE = "존재하지 않는 역입니다.";
    private static final String REQUIRE_STATION_NAME_EXCEPTION_MESSAGE = "다음역의 이름을 입력해주세요.";
    private static final String REQUIRE_LINE_NAME_EXCEPTION_MESSAGE = "노선의 이름을 입력해주세요.";

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest response) {
        Line line = response.toLine();
        validateLine(line);
        return LineResponse.of(lineRepository.save(line), findStationsAtLine(line));
    }

    private void validateLine(Line line) {
        if (line.getName() == null) {
            throw new IllegalArgumentException(REQUIRE_LINE_NAME_EXCEPTION_MESSAGE);
        }

        lineRepository.findByName(line.getName())
                .ifPresent(value -> {
                    throw new IllegalArgumentException(DUPLICATED_STATION_EXCEPTION_MESSAGE);
                });
    }

    public LineResponse showLine(Long id) {
        Line line = findLine(id);
        return LineResponse.of(line, findStationsAtLine(line));
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                .stream()
                .map(line -> LineResponse.of(line, findStationsAtLine(line)))
                .collect(Collectors.toList());
    }

    public LineResponse updateLine(Long id, LineUpdateRequest request) {
        Line updatedLine = request.toLine();
        Line originalLine = findLine(id);
        originalLine.update(updatedLine);
        Line persistLine = lineRepository.save(originalLine);
        return LineResponse.of(persistLine, findStationsAtLine(persistLine));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public StationsAtLineResponse addEdge(Long lineId, @Valid EdgeCreateRequest request) {
        Line line = findLine(lineId);
        Edge edge = toEdge(request);
        //TODO: edge의 prestation, stationid 존재하는지 체크
        line.addEdge(edge);

        Line savedLine = lineRepository.save(line);
        List<Station> stations = findStationsAtLine(savedLine);
        return new StationsAtLineResponse(savedLine, stations);
    }

    public List<StationsAtLineResponse> findEveryLineAndStation() {
        List<StationsAtLineResponse> response = new ArrayList<>();
        List<Line> lines = lineRepository.findAll();

        for (Line line : lines) {
            List<Station> stations = findStationsAtLine(line);
            response.add(new StationsAtLineResponse(line.getId(), line.getName(), line.getBgColor(), stations));
        }
        return response;
    }

    public List<Station> findStationsAtLine(Line savedLine) {
        Edges edges = savedLine.getEdges();
        List<Station> stations = new ArrayList<>();

        for (Edge edge : edges) {
            stations.add(findStation(edge.getStationId()));
        }
        return stations;
    }

    public void removeEdge(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        line.removeEdge(stationId);
        lineRepository.save(line);
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION_MESSAGE));
    }

    private Station findStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(REQUIRE_STATION_NAME_EXCEPTION_MESSAGE));
    }

    private Edge toEdge(EdgeCreateRequest request) {
        Long preStationId = stationRepository.findIdByName(request.getPreStationName());
        Long stationId = stationRepository.findIdByName(request.getStationName());
        if ((request.getPreStationName() != null && preStationId == null)) {
            throw new IllegalArgumentException(NO_SUCH_EDGE_EXCEPTION_MESSAGE);
        }
        if (stationId == null) {
            throw new IllegalArgumentException(REQUIRE_STATION_NAME_EXCEPTION_MESSAGE);
        }
        return new Edge(preStationId, stationId, request.getDistance(), request.getDuration());
    }
}
