package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.exception.DuplicateLineException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getTitle(), lineRequest.getStartTime(), lineRequest.getEndTime(),
                lineRequest.getIntervalTime(), lineRequest.getBgColor());
        lineRepository.findByTitle(line.getTitle())
                .ifPresent(this::throwDuplicateException);

        Line saveLine = lineRepository.save(line);
        return LineResponse.of(saveLine);
    }

    private void throwDuplicateException(Line line) {
        throw new DuplicateLineException(line.getTitle());
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = new Line(lineRequest.getTitle(), lineRequest.getStartTime(), lineRequest.getEndTime(),
                lineRequest.getIntervalTime(), lineRequest.getBgColor());
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(line);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public List<Station> showStations() {
        return stationRepository.findAll();
    }

    public void addEdge(Long lineId, EdgeCreateRequest request) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        Long preStationId = stationRepository.findIdByName(request.getPreStationName()).orElse(null);
        Long stationId = stationRepository.findIdByName(request.getStationName()).orElseThrow(NoSuchElementException::new);
        line.addEdge(new Edge(preStationId, stationId, request.getDistance(), request.getDuration()));
        lineRepository.save(line);
    }

    public List<LineResponse> findAllStationsWithLine() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(Line::getId)
                .map(this::findStationsByLineId)
                .collect(Collectors.toList());
    }

    public LineResponse findStationsByLineId(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        List<Station> stations = new ArrayList<>();

        List<Edge> edges = line.getSortedEdges();
        for (Edge edge : edges) {
            checkAllStationById(line, stations, edge);
        }
        return LineResponse.of(line, stations);
    }

    private void checkAllStationById(Line line, List<Station> stations, Edge edge) {
        for (Station station : stationRepository.findAllById(line.getEdgeIds())) {
            checkSameId(stations, edge, station);
        }
    }

    private void checkSameId(List<Station> stations, Edge edge, Station station) {
        if (station.getId() == edge.getStationId()) {
            stations.add(station);
        }
    }

    public void deleteStationByLineIdAndStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        line.removeEdgeById(stationId);
        lineRepository.save(line);
    }
}