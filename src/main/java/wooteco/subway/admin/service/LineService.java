package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(findLineWithStationsById(line.getId()));
        }
        return lineResponses;
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Line addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당되는 노선을 찾을 수 없습니다."));
        line.addLineStation(request.toLineStation());
        return lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당되는 노선을 찾을 수 없습니다."));
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당되는 노선을 찾을 수 없습니다."));
        return LineResponse.of(line, findStationsByLineId(id));
    }

    public List<StationResponse> findStationsByLineId(Long id) {
        List<StationResponse> stations = new ArrayList<>();
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당되는 노선을 찾을 수 없습니다."));
        List<Long> stationsIds = line.getLineStationsId();

        for (int i = 0; i < stationsIds.size(); i++) {
            Optional<Station> foundStation = stationRepository.findById(stationsIds.get(i));
            foundStation.ifPresent(station -> stations.add(StationResponse.of(station)));
        }
        return stations;
    }
}
