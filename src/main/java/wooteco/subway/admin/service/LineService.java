package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.LineStationFinder;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.AlreadyExistDataException;
import wooteco.subway.admin.exception.NotExistDataException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        if (lineRepository.isExistLineByName(line.getName())) {
            throw new AlreadyExistDataException("이미 등록된 노선입니다!");
        }
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        if (persistLine.isNotEqualName(line) && lineRepository.isExistLineByName(line.getName())) {
            throw new AlreadyExistDataException("이미 등록된 노선입니다!");
        }
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public Line addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(NotExistDataException::new);
        LineStation lineStation = request.toLineStation();
        line.addLineStation(lineStation);
        return lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findStationsByLineId(Long id) {
        Line line = findById(id);
        return findStationsByLine(line);
    }

    public LineResponse findStationsByLine(Line line) {
        List<Station> stations = line.findDepartureLineStation()
                .map(departureLineStation -> createOrderedStations(line))
                .orElse(Collections.emptyList());
        return LineResponse.withStations(line, new LinkedHashSet<>(stations));
    }

    public List<LineResponse> findAllLinesWithStations() {
        return showLines().stream()
                .map(this::findStationsByLine)
                .collect(Collectors.toList());
    }

    private List<Station> createOrderedStations(Line line) {
        List<Station> orderedStations = new ArrayList<>();
        Map<Long, Station> stationFinder = createStationFinder(line);
        LineStationFinder lineStationFinder = line.createLineStationFinder();
        Long nextStationId = null;

        while (lineStationFinder.hasMore()) {
            LineStation currentLineStation = lineStationFinder.popByPreStationId(nextStationId);

            Long stationId = currentLineStation.getStationId();
            Station station = stationFinder.get(stationId);

            orderedStations.add(station);

            nextStationId = currentLineStation.getStationId();
        }
        return orderedStations;
    }

    private Map<Long, Station> createStationFinder(Line line) {
        List<Long> stationIds = line.getLineStations().stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        Map<Long, Station> stationFinder = new HashMap<>();
        Iterable<Station> stations = stationRepository.findAllById(stationIds);
        for (Station station : stations) {
            stationFinder.put(station.getId(), station);
        }
        return stationFinder;
    }

    public Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(NotExistDataException::new);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
