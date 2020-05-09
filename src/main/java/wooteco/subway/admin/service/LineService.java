package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class LineService {
    private static final String NO_SUCH_LINE = "해당 ID의 노선이 없습니다.";

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        return lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStation lineStation) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_SUCH_LINE));
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException(NO_SUCH_LINE));
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_SUCH_LINE));
        return LineResponse.of(line, findStationsOf(line));
    }

    private Set<Station> findStationsOf(Line line) {
        Set<Station> stations = new HashSet<>();
        for (Station station : stationRepository.findAllById(line.getLineStationsId())) {
            stations.add(station);
        }
        return stations;
    }
}
