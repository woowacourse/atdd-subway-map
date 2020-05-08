package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

    public void addLineStation(Long lineId, LineStationCreateRequest request) {
        // TODO: 구현
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.addLineStation(request.toEntity());
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 id입니다"));

        List<Long> stationsId = line.getStations().stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());

        return LineResponse.of(line, (ArrayList<Station>)stationRepository.findAllById(stationsId));
    }

    public LineResponse findByName(String name) {
        Line line = lineRepository.findByName(name)
            .orElseThrow(()->new IllegalArgumentException("잘못된 이름입니다."));

        return LineResponse.of(line);
    }
}
