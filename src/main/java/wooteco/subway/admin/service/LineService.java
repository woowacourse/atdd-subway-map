package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) throws SQLException {
        return lineRepository.save(line);
    }

    public List<LineWithStationsResponse> showLines() {
        List<Line> lines = lineRepository.findAll();

        List<List<Long>> stationIdsPerLines = lines.stream()
                .map(Line::getLineStationsId)
                .collect(Collectors.toList());

        List<List<Station>> collect = stationIdsPerLines.stream()
                .map(stationIds -> stationRepository.findAllById(stationIds))
                .collect(Collectors.toList());

        List<LineWithStationsResponse> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            result.add(LineWithStationsResponse.of(lines.get(i), collect.get(i)));
        }

        return result;
    }

    public Line updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        persistLine.update(line);
        return lineRepository.save(persistLine);
    }

    public void deleteLineBy(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long lineId, LineStationCreateRequest lineStationCreateRequest) {
        Line persistLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

        if (lineStationCreateRequest.getPreStationId() == null) {
            persistLine.addLineStationOnFirst(lineStationCreateRequest.toLineStation());
            lineRepository.save(persistLine);
            return;
        }

        persistLine.addLineStation(lineStationCreateRequest.toLineStation());
        lineRepository.save(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }

    public LineWithStationsResponse findLineWithStationsBy(Long lineId) {
        Line persistLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

        List<Long> stationIds = persistLine.getLineStationsId();

        List<Station> stations = stationRepository.findAllById(stationIds);

        return LineWithStationsResponse.of(persistLine, stations);
    }
}
