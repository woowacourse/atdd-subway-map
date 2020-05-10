package wooteco.subway.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    @Autowired
    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<LineResponse> findAllLines() {
        return LineResponse.listOf(lineRepository.findAll());
    }

    public Line findLineById(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        LineStation lineStation = new LineStation(request.getLine(), request.getPreStationId(), request.getStationId(),
                request.getDistance(), request.getDuration());
        line.addLineStation(lineStation);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);

        line.removeLineStationById(stationId);
    }

    public LineResponse findLineWithStationsById(final Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        List<Station> listStations = stationRepository.findAllById(line.getLineStationsId());

        Set<Station> stations = new LinkedHashSet<>(
                line.getLineStationsId().stream()
                        .map(stationId -> listStations.stream()
                                .filter(station -> station.getId().equals(stationId))
                                .findFirst()
                                .orElseThrow(NoSuchElementException::new))
                        .collect(Collectors.toList())
        );

        LineResponse lineResponse = LineResponse.of(line);
        lineResponse.updateStations(stations);
        return lineResponse;
    }
}
