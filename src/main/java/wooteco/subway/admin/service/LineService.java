package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.line.Line;
import wooteco.subway.admin.domain.station.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
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

    @Transactional
    public Long save(Line line) {
        return lineRepository.save(line).getId();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLineWithStations() {
        List<LineResponse> result = new ArrayList<>();
        List<Line> lines = lineRepository.findAll();

        for (Line line : lines) {
            List<Station> stations = stationRepository.findAllById(line.getStationsId());
            Map<Long, Station> stationMap = stations.stream()
                .collect(toMap(Station::getId, Function.identity()));

            List<Long> sortedStationsId = line.getSortedStationsId();
            List<Station> sortedStations = sortedStationsId.stream()
                .map(stationMap::get)
                .collect(toList());

            result.add(LineResponse.of(line, sortedStations));
        }
        return result;
    }

    @Transactional
    public void updateLine(Long id, Line line) {
        Line persistLine = findById(id);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findById(id);
        line.addLineStation(request.toLineStation());
        lineRepository.save(line);
    }

    @Transactional
    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long id) {
        Line line = findById(id);
        List<Station> stations = stationRepository.findAllById(line.getSortedStationsId());
        return LineResponse.of(line, stations);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 노선입니다. id = " + id));
    }
}
