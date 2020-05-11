package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;

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

    public LineResponse save(Line line) {
        if (lineRepository.findLineWithStationsByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("중복된 지하철 역입니다. name = " + line.getName());
        }
        return LineResponse.of(lineRepository.save(line));
    }

    public List<LineResponse> findAllLineWithStations() {
        List<LineResponse> result = new ArrayList<>();
        List<Line> lines = lineRepository.findAll();

        for (Line line : lines) {
            Set<Station> stations = line.getSortedStationsId()
                .stream()
                .map(stationId -> stationRepository.findById(stationId)
                    .orElseThrow(AssertionError::new))
                .collect(toCollection(LinkedHashSet::new));

            result.add(LineResponse.of(line, stations));
        }

        return result;
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        if (lineRepository.findLineWithStationsByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("중복된 지하철 역입니다. name = " + line.getName());
        }
        persistLine.update(line);
        return LineResponse.of(lineRepository.save(persistLine));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("존재 하지 않는 노선입니다. id = " + id));
        line.addLineStation(request.toLineStation());
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new NoSuchElementException("존재 하지 않는 노선입니다. id = " + lineId));
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("존재 하지 않는 Line 입니다. id=" + id));

        Set<Station> stations = stationRepository.findAllById(line.getSortedStationsId());
        return LineResponse.of(line, stations);
    }
}
