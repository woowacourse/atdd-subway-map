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

    public Line create(Line line) {
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

    public Line updateLine(Long id, Line line) {
        Line persistLine = getPersistLine(id);
        System.out.println("###########################");
        persistLine.update(line);
        return lineRepository.save(persistLine);
    }

    private Line getPersistLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Line addLineStation(Long id, LineStationCreateRequest request) {
        Line persistLine = getPersistLine(id);
        persistLine.addLineStation(request.toLineStation());
        return lineRepository.save(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = getPersistLine(lineId);
        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line persistLine = getPersistLine(id);
        return LineResponse.of(persistLine, findStationsByLineId(id));
    }

    public List<StationResponse> findStationsByLineId(Long id) {
        List<StationResponse> stations = new ArrayList<>();
        Line persistLine = getPersistLine(id);
        List<Long> stationsIds = persistLine.getLineStationsId();

        for (int i = 0; i < stationsIds.size(); i++) {
            Optional<Station> foundStation = stationRepository.findById(stationsIds.get(i));
            foundStation.ifPresent(station -> stations.add(StationResponse.of(station)));
        }
        return stations;
    }
}
