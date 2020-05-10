package wooteco.subway.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
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

    public LineResponse save(Line line) {
        return LineResponse.of(lineRepository.save(line));
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

    public LineResponse addLineStation(Long lineId, LineStationCreateRequest request) {
        Line persistLine = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        persistLine.addLineStation(request.toLineStation());

        return LineResponse.of(lineRepository.save(persistLine));
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
        return lineRepository.findAll()
            .stream()
            .map(line -> findLineStationsById(line.getId()))
            .collect(Collectors.toList());
    }

    public LineStationsResponse findLineStationsById(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        List<Long> lineStations = line.getStations()
            .stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());

        Map<Long, Station> stations = stationRepository.findAllById(lineStations)
            .stream()
            .collect(Collectors.toMap(
                Station::getId,
                station -> station));

        return LineStationsResponse.of(line, stations);
    }

    public StationResponse createStation(StationCreateRequest stationCreateRequest) {
        Station station = stationCreateRequest.toStation();

        return StationResponse.of(stationRepository.save(station));
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll()
            .stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public void deleteStationById(Long stationId) {
        stationRepository.deleteById(stationId);
    }
}
