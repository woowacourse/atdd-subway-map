package wooteco.subway.admin.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.line.domain.Line;
import wooteco.subway.admin.line.domain.Lines;
import wooteco.subway.admin.line.domain.edge.Edges;
import wooteco.subway.admin.line.domain.repository.LineRepository;
import wooteco.subway.admin.line.service.dto.edge.EdgeCreateRequest;
import wooteco.subway.admin.line.service.dto.edge.EdgeDeleteRequest;
import wooteco.subway.admin.line.service.dto.edge.EdgeResponse;
import wooteco.subway.admin.line.service.dto.line.LineEdgeResponse;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.domain.Stations;
import wooteco.subway.admin.station.domain.repository.StationRepository;

import java.util.ArrayList;
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
    public List<LineResponse> getLineResponses() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line, stationRepository.findAllById(line.getEdgesStationIds())))
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

    @Transactional(readOnly = true)
    public List<EdgeResponse> findEdgesByLineId(Long lineId) {
        Line line = findLineById(lineId);

        Stations stations = new Stations(stationRepository.findAllById(line.getEdgesStationIds()));

        Edges edges = line.getEdges();
        return EdgeResponse.listOf(edges.getEdges(), stations);
    }

    private Line findLineById(final Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(lineId + " : 존재하지 않는 노선값 입니다."));
    }

    @Transactional
    public void addEdge(Long lineId, EdgeCreateRequest request) {
        Line line = findLineById(lineId);

        checkExistStationIds(request);

        line.addEdge(request.toEdge());
        lineRepository.save(line);
    }

    private void checkExistStationIds(final EdgeCreateRequest request) {
        Stations stations = new Stations(stationRepository.findAllById(request.getAllStationId()));

        for (Long stationId : request.getAllStationId()) {
            stations.findById(stationId);
        }
    }

    @Transactional
    public void removeEdge(Long lineId, EdgeDeleteRequest edgeDeleteRequest) {
        Line line = findLineById(lineId);
        line.removeLineStationById(edgeDeleteRequest.getStationId());
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(line, stationRepository.findAllById(line.getEdgesStationIds()));
    }

    @Transactional(readOnly = true)
    public List<LineEdgeResponse> getAllLineEdge() {
        Lines lines = new Lines(lineRepository.findAll());
        Stations stations = new Stations(stationRepository.findAllById(lines.getAllEdgeStationId()));

        List<LineEdgeResponse> lineEdgeResponses = new ArrayList<>();
        for (Line line : lines) {
            Edges edges = line.getEdges();
            List<EdgeResponse> edgeResponses = EdgeResponse.listOf(edges.getEdges(), stations);
            lineEdgeResponses.add(new LineEdgeResponse(line, edgeResponses));
        }
        return lineEdgeResponses;
    }
}
