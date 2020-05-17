package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.NoSuchElementException;
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

    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
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
        if(request.hasNotAnyId()) {
            convertNameToId(request);
        }
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        LineStation lineStation = request.toLineStation();

        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    private void convertNameToId(LineStationCreateRequest request) {
        if(!request.getPreStationName().isEmpty()) {
            Station preStation = stationRepository.findByName(request.getPreStationName())
                    .orElseThrow(IllegalArgumentException::new);
            request.setPreStationId(preStation.getId());
        }
        Station station = stationRepository.findByName(request.getStationName())
                .orElseThrow(IllegalArgumentException::new);
        request.setStationId(station.getId());

    }

    public Line findLineById(long id) {
        return lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public void deleteStationByLineIdAndStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public List<Long> findAllLineId(List<Line> lines) {
        return lines.stream()
                .map(Line::getId)
                .collect(Collectors.toList());
    }

    public List<Station> findAllById(List<Long> allLineId) {
        return stationRepository.findAllById(allLineId);
    }
}