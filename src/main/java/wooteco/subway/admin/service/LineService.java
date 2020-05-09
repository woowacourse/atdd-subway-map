package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.LineStationAddRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.StationsAtLineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

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

    public LineResponse save(Line line) {
        validate(line);
        return LineResponse.of(lineRepository.save(line));
    }

    private void validate(Line line) {
        List<Line> savedLines = lineRepository.findAll();
        for (Line savedLine : savedLines) {
            if (savedLine.getName().equals(line.getName())) {
                throw new IllegalArgumentException("중복되는 역이 존재합니다.");
            }
        }
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        return LineResponse.of(line);
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

    public StationsAtLineResponse addLineStation(Long id, LineStationAddRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        LineStation lineStation = createLineStation(request);
        line.addLineStation(lineStation);

        Line savedLine = lineRepository.save(line);
        Set<Station> stations = getStationsAtLine(savedLine);
        return new StationsAtLineResponse(savedLine, stations);
    }

    private LineStation createLineStation(LineStationAddRequest request) {
        Long preStationId = stationRepository.findIdByName(request.getPreStationName());
        Long stationId = stationRepository.findIdByName(request.getStationName());
        return new LineStation(preStationId, stationId, request.getDistance(), request.getDuration());
    }

    private Set<Station> getStationsAtLine(Line savedLine) {
        Set<LineStation> lineStations = savedLine.getStations();
        Set<Station> stations = new LinkedHashSet<>();

        for (LineStation ls : lineStations) {
            if (ls.getPreStationId() != null) {
                stations.add(stationRepository.findById(ls.getPreStationId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다.")));
            }
            stations.add(stationRepository.findById(ls.getStationId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다.")));
        }
        return stations;
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(IllegalArgumentException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
        stationRepository.deleteById(stationId);
    }

    public LineResponse findLineWithStationsById(Long id) {
        // TODO: 구현
        return new LineResponse();
    }

    public List<StationsAtLineResponse> findAllLineStations() {
        List<StationsAtLineResponse> response = new ArrayList<>();

        List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            Set<Station> stations = getStationsAtLine(line);
            response.add(new StationsAtLineResponse(line.getId(), line.getName(), stations));
        }
        return response;
    }
}
