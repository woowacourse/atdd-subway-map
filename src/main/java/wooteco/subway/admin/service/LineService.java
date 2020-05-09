package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(Line line) {
        Line saveLine = lineRepository.save(line);
        return LineResponse.of(saveLine);
    }

    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public List<LineResponse> showLines() {
        List<Line> lines =  lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        return LineResponse.of(lineRepository.save(persistLine));
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

    public void addLineStation(Long id, LineStationCreateRequest request) {
        // TODO: 구현
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
    }

    public LineResponse findLineWithStationsById(Long id) {
        // TODO: 구현
        return new LineResponse();
    }
}
