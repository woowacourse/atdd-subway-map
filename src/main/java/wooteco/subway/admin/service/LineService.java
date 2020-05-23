package wooteco.subway.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Transactional
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
        List<LineResponse> lineResponses = new ArrayList<>();
        final List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            lineResponses.add(
                LineResponse.convert(line, findStationsByStationIds(line.findLineStationsId())));
        }
        return lineResponses;
    }

    public void updateLine(Long lineId, Line line) {
        Line persistLine = findLineByLineId(lineId);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    private Line findLineByLineId(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
    }

    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findLineByLineId(id);
        line.addLineStation(request.toLineStation());
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findLineByLineId(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsByLineId(Long lineId) {
        final Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 노선이 존재하지 않습니다."));
        return LineResponse.convert(line,
            findStationsByStationIds(line.findLineStationsId()));
    }

    public Set<Station> findStationsByStationIds(List<Long> stationIds) {
        return stationRepository.findAllById(stationIds);
    }

    public List<StationResponse> findStationResponsesWithLineId(Long lineId) {
        final LineResponse lineResponse = findLineWithStationsByLineId(lineId);
        final Set<Station> stations = lineResponse.getStations();
        List<StationResponse> stationResponses = new ArrayList<>();
        for (Station station : stations) {
            stationResponses.add(StationResponse.of(station));
        }
        return stationResponses;
    }

    public Station saveStation(Station station) {
        return stationRepository.save(station);
    }

    public List<Station> findAllStations() {
        return stationRepository.findAll();
    }

    public void deleteStationById(Long stationId) {
        stationRepository.deleteById(stationId);
    }

    public Long findStationIdByName(String name) {
        return stationRepository.findIdByName(name);
    }
}
