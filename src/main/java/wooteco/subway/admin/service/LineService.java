package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationDto;
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
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
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
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        LineStation lineStation = request.toLineStation();
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public LineResponse registerLineStation(LineStationDto lineStationDto) {
        String name = lineStationDto.getName();
        Line line = lineRepository.findByName(name);

        String preStationName = lineStationDto.getPreStationName();
        Station preStation = stationRepository.findByName(preStationName);

        String arrivalStationName = lineStationDto.getArrivalStationName();
        Station arrivalStation = stationRepository.save(new Station(arrivalStationName));

        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(preStation.getId(), arrivalStation.getId(), 10, 10);
        addLineStation(line.getId(), lineStationCreateRequest);

        List<Long> stationIds = line.getStations().stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        Set<Station> stations = new HashSet<>((List<Station>) stationRepository.findAllById(stationIds));


        return LineResponse.withStations(line, stations);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
        // TODO: 구현
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalStateException::new);

        List<Long> stationIds = line.getStations().stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        Set<Station> stations = new HashSet<>((List<Station>) stationRepository.findAllById(stationIds));
        return LineResponse.withStations(line, stations);

    }
}
