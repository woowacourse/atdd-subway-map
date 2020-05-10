package wooteco.subway.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationsResponse;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        return lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Line addLineStation(Long lineId, LineStationCreateRequest request) {
        Line persistLine = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        persistLine.addLineStation(request.toLineStation());
        return lineRepository.save(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = lineRepository.findById(lineId)
                .orElseThrow(RuntimeException::new);
        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }

    public LineResponse findLineById(Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);

        return LineResponse.of(persistLine);
    }

    public List<LineStationsResponse> findAllLineStations() {
        Map<Long, Station> stations = generateStationMapper();
        return lineRepository.findAll()
                .stream()
                .map(line -> LineStationsResponse.of(line, stations))
                .collect(Collectors.toList());
    }

    public LineStationsResponse findLineStationsById(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(RuntimeException::new);
        Map<Long, Station> stations = generateStationMapper();
        return LineStationsResponse.of(line, stations);
    }

    private Map<Long, Station> generateStationMapper() {
        return stationRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Station::getId,
                        station -> station));
    }

    public Station createStation(StationCreateRequest stationCreateRequest) {
        Station station = stationCreateRequest.toStation();
        return stationRepository.save(station);
    }

    public List<StationResponse> findAllStations() {
        List<Station> persistStations = stationRepository.findAll();
        return StationResponse.listOf(persistStations);
    }

    public void deleteStationById(Long stationId) {
        stationRepository.deleteById(stationId);
    }
}
