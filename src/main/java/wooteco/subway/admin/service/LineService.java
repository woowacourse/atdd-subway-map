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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    private static final String NO_LINE_EXCEPTION = "존재하지 않는 노선입니다.";
    private static final String DUPLICATE_LINE_NAME_EXCEPTION = "중복되는 역이 존재합니다.";
    private static final String NO_STATION_EXCEPTION = "존재하지 않는 역입니다.";

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(Line line) {
        validateName(line);
        return LineResponse.of(lineRepository.save(line));
    }

    private void validateName(Line line) {
        if (lineRepository.findName(line.getName()) > 0) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_EXCEPTION);
        }
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_LINE_EXCEPTION));
        return LineResponse.of(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_LINE_EXCEPTION));
        persistLine.update(line);
        return LineResponse.of(lineRepository.save(persistLine));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public StationsAtLineResponse addLineStation(Long id, LineStationAddRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_LINE_EXCEPTION));
        LineStation lineStation = createLineStation(request);
        line.addLineStation(lineStation);

        Line savedLine = lineRepository.save(line);
        List<Station> stations = findStationsAtLine(savedLine);
        return new StationsAtLineResponse(savedLine, stations);
    }

    private LineStation createLineStation(LineStationAddRequest request) {
        Long preStationId = stationRepository.findIdByName(request.getPreStationName());
        Long stationId = stationRepository.findIdByName(request.getStationName());
        return new LineStation(preStationId, stationId, request.getDistance(), request.getDuration());
    }

    public List<Station> findStationsAtLine(Line line) {
        List<Long> ids = line.getStations()
                .stream()
                .mapToLong(LineStation::getStationId)
                .boxed()
                .collect(Collectors.toList());

        return stationRepository.findAllByIds(ids);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(NO_LINE_EXCEPTION));
        line.removeLineStationById(stationId);
        lineRepository.save(line);
        stationRepository.deleteById(stationId);
    }

    public List<StationsAtLineResponse> findAllLineStations() {
        List<StationsAtLineResponse> response = new ArrayList<>();

        List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            List<Station> stations = findStationsAtLine(line);
            response.add(new StationsAtLineResponse(line.getId(), line.getName(), line.getBgColor(), stations));
        }
        return response;
    }
}
