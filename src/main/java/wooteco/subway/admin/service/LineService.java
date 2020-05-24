package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Edges;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.domain.exception.DuplicatedLineException;
import wooteco.subway.admin.domain.exception.NoSuchLineException;
import wooteco.subway.admin.domain.exception.NoSuchStationException;
import wooteco.subway.admin.domain.exception.RequireStationNameException;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest request) {
        Line line = request.toLine();
        lineRepository.findByName(line.getName())
                .ifPresent(val -> {
                    throw new DuplicatedLineException();
                });
        return LineResponse.of(lineRepository.save(line), findStationsAtLine(line));
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

    public StationsAtLineResponse addEdge(Long lineId, EdgeCreateRequest request) {
        Line line = findLine(lineId);
        Edge edge = toEdge(request);
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
            response.add(new StationsAtLineResponse(line, stations));
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
                .orElseThrow(NoSuchLineException::new);
    }

    private Station findStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(RequireStationNameException::new);
    }

    private Edge toEdge(@Valid EdgeCreateRequest request) {
        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(NoSuchStationException::new);
        if (Objects.isNull(request.getPreStationId())) {
            return Edge.ofFirst(station.getId(), request.getDistance(), request.getDuration());
        }
        Station preStation = stationRepository.findById(request.getPreStationId())
                .orElseThrow(NoSuchStationException::new);
        return Edge.of(preStation.getId(), station.getId(), request.getDistance(), request.getDuration());
    }
}