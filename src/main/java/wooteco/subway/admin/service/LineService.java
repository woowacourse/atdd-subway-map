package wooteco.subway.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
            .map(line -> LineResponse.of(line, mapLineStationsToStations(line.getStations())))
            .collect(Collectors.toList());
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineStationResponse addLineStation(LineStationRequest request) {
        // TODO: 구현
        Line line = lineRepository.findById(request.getLineId())
            .orElseThrow(IllegalArgumentException::new);
        LineStation lineStation = request.toEntity();
        line.addLineStation(lineStation);
        Line persistLine = lineRepository.save(line);
        return new LineStationResponse(request.getLineId(), lineStation.getPreStationId(),
            lineStation.getStationId(), lineStation.getDistance(), lineStation.getDuration());
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("잘못된 id입니다"));

        List<Long> stationsId = line.getStations().stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());

        return LineResponse.of(line, (ArrayList<Station>)stationRepository.findAllById(stationsId));
    }

    public LineResponse findByName(String name) {
        Line line = lineRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("잘못된 이름입니다."));

        return LineResponse.of(line);
    }

    public List<Station> mapLineStationsToStations(List<LineStation> lineStations) {
        return lineStations.stream()
            .map(lineStation -> stationRepository.findById(lineStation.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 id입니다.")))
            .collect(Collectors.toList());
    }
}
