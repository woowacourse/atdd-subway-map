package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
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

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    public List<LineResponse> showLinesWithStations() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(this::createLineResponseWithStations)
            .collect(Collectors.toList());
    }

    private LineResponse createLineResponseWithStations(Line line) {
        List<Station> stations = stationRepository.findAllByIdOrderBy(line.getId());
        return LineResponse.of(line, StationResponse.listOf(stations));
    }

    public LineResponse save(Line line) {
        validateDuplicateName(line);
        return LineResponse.of(lineRepository.save(line));
    }

    private void validateDuplicateName(Line line) {
        boolean sameName = lineRepository.findByName(line.getName()).isPresent();
        if (sameName) {
            throw new IllegalArgumentException("중복되는 역 이름입니다.");
        }
    }

    public LineResponse findLineResponseById(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = findLineById(id);
        persistLine.update(line);

        Line updatedLine = lineRepository.save(persistLine);
        return LineResponse.of(updatedLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long lineId, LineStationCreateRequest request) {
        if (request.hasNotAnyId()) {
            convertNameToId(request);
        }
        Line line = findLineById(lineId);
        LineStation lineStation = request.toLineStation();
        line.addLineStation(lineStation);

        lineRepository.save(line);
    }

    private void convertNameToId(LineStationCreateRequest request) {
        if (!request.getPreStationName().isEmpty()) {
            Station preStation = findStationByName(request.getPreStationName());
            request.setPreStationId(preStation.getId());
        }
        Station station = findStationByName(request.getStationName());
        request.setStationId(station.getId());
    }

    private Station findStationByName(String stationName) {
        return stationRepository.findByName(stationName)
            .orElseThrow(
                () -> new NoSuchElementException(String.format("%s역이 등록되어 있지 않습니다.", stationName)));
    }

    public LineResponse findLineResponseWithStationsById(Long id) {
        Line line = findLineById(id);
        return createLineResponseWithStations(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        line.removeLineStationById(stationId);

        lineRepository.save(line);
    }
}