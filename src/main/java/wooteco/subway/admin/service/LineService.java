package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.EdgeAddRequest;
import wooteco.subway.admin.dto.request.LineRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.StationsAtLineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String DUPLICATED_STATION_EXCEPTION_MESSAGE = "중복되는 역이 존재합니다.";
    private static final String NO_LINE_EXCEPTION_MESSAGE = "존재하지 않는 노선입니다.";
    private static final String NO_STATION_EXCEPTION_MESSAGE = "존재하지 않는 역입니다.";
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
        return LineResponse.of(lineRepository.save(line));
    }

    private void validateLine(Line line) {
        List<Line> savedLines = lineRepository.findAll();
        if (line.getName() == null) {
            throw new IllegalArgumentException(REQUIRE_LINE_NAME_EXCEPTION_MESSAGE);
        }
        for (Line savedLine : savedLines) {
            if (savedLine.getName().equals(line.getName())) {
                throw new IllegalArgumentException(DUPLICATED_STATION_EXCEPTION_MESSAGE);
            }
        }
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_LINE_EXCEPTION_MESSAGE));
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

    public StationsAtLineResponse addEdge(Long id, EdgeAddRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        Edge edge = createEdge(request);
        line.addEdge(edge);

        Line savedLine = lineRepository.save(line);
        List<Station> stations = findStationsAtLine(savedLine);
        return new StationsAtLineResponse(savedLine, stations);
    }

    private Edge createEdge(EdgeAddRequest request) {
        Long preStationId = stationRepository.findIdByName(request.getPreStationName());
        Long stationId = stationRepository.findIdByName(request.getStationName());
        return new Edge(preStationId, stationId, request.getDistance(), request.getDuration());
    }

    public List<StationsAtLineResponse> findAllEdges() {
        List<StationsAtLineResponse> response = new ArrayList<>();

        List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            List<Station> stations = findStationsAtLine(line);
            response.add(new StationsAtLineResponse(line.getId(), line.getName(), line.getBgColor(), stations));
        }
        return response;
    }

    public List<Station> findStationsAtLine(Line savedLine) {
        List<Edge> edges = savedLine.getStations();
        List<Station> stations = new ArrayList<>();

        for (Edge ls : edges) {
            stations.add(stationRepository.findById(ls.getStationId())
                    .orElseThrow(() -> new IllegalArgumentException(NO_STATION_EXCEPTION_MESSAGE)));
        }
        return stations;
    }

    public void removeEdge(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(IllegalArgumentException::new);
        line.removeEdgeById(stationId);
        lineRepository.save(line);
        stationRepository.deleteById(stationId);
    }
}
