package wooteco.subway.admin.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.line.domain.Line;
import wooteco.subway.admin.line.domain.Lines;
import wooteco.subway.admin.line.domain.edge.Edge;
import wooteco.subway.admin.line.domain.edge.Edges;
import wooteco.subway.admin.line.domain.repository.LineRepository;
import wooteco.subway.admin.line.service.dto.edge.EdgeCreateRequest;
import wooteco.subway.admin.line.service.dto.edge.EdgeDeleteRequest;
import wooteco.subway.admin.line.service.dto.edge.EdgeResponse;
import wooteco.subway.admin.line.service.dto.line.LineCreateRequest;
import wooteco.subway.admin.line.service.dto.line.LineEdgeResponse;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.domain.Stations;
import wooteco.subway.admin.station.domain.repository.StationRepository;

import java.util.List;
import java.util.function.Supplier;
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
    public Long save(LineCreateRequest lineCreateRequest) {
        Line line = new Line(lineCreateRequest.getName(), lineCreateRequest.getStartTime(),
                lineCreateRequest.getEndTime(), lineCreateRequest.getIntervalTime(), lineCreateRequest.getBgColor());
        lineRepository.findByName(line.getName())
                .ifPresent(this::throwAlreadyExistNameException);

        Line persistLine = lineRepository.save(line);
        return persistLine.getId();
    }

    public void throwAlreadyExistNameException(Line line) {
        throw new IllegalArgumentException(String.format("%s 이미 존재하는 노선 이름입니다.", line.getName()));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLineResponses() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line, stationRepository.findAllById(line.getEdgesStationIds())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLine(Long id, Line line) {
        Line persistLine = findLineById(id);

        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    private Supplier<IllegalArgumentException> throwNotExistEntityException(final Long id) {
        return () -> new IllegalArgumentException(id + "존재하지 않는 id 값 입니다.");
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EdgeResponse> findEdgesByLineId(Long lineId) {
        Line line = findLineById(lineId);

        Stations stations = new Stations(stationRepository.findAllById(line.getEdgesStationIds()));

        Edges edges = line.getEdges();
        return EdgeResponse.listOf(edges, stations);
    }

    private Line findLineById(final Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(throwNotExistEntityException(lineId));
    }

    @Transactional
    public void addEdge(Long lineId, EdgeCreateRequest request) {
        Line line = findLineById(lineId);

        Stations stations = new Stations(stationRepository.findAllById(request.getAllStationId()));
        stations.checkCreatableEdge(request.getAllStationId());

        Edge edge = new Edge(request.getPreStationId(), request.getStationId(), request.getDistance(), request.getDuration());
        line.addEdge(edge);
        lineRepository.save(line);
    }

    @Transactional
    public void removeEdge(Long lineId, EdgeDeleteRequest edgeDeleteRequest) {
        Line line = findLineById(lineId);
        line.removeLineStationById(edgeDeleteRequest.getStationId());
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long id) {
        Line line = findLineById(id);

        return LineResponse.of(line, stationRepository.findAllById(line.getEdgesStationIds()));
    }

    @Transactional(readOnly = true)
    public List<LineEdgeResponse> getAllLineEdge() {
        Lines lines = new Lines(lineRepository.findAll());
        Stations stations = new Stations(stationRepository.findAllById(lines.getAllEdgeStationId()));

        return LineEdgeResponse.listOf(lines, stations);
    }
}
