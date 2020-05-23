package wooteco.subway.admin.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateByNameRequest;
import wooteco.subway.admin.exceptions.DuplicateLineException;
import wooteco.subway.admin.exceptions.LineNotFoundException;
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

    public Line save(Line line) {
        validateDuplicateName(line);
        return lineRepository.save(line);
    }

    private void validateDuplicateName(Line line) {
        if (lineRepository.countDistinctByName(line.getName()) != 0) {
            throw new DuplicateLineException(line.getName());
        }
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = getLineById(id);
        persistLine.update(line);
        Line updatedLine = lineRepository.save(persistLine);
        return LineResponse.of(updatedLine, findStationsOf(updatedLine));
    }

    public void deleteLineById(Long id) {
        getLineById(id);
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStation lineStation) {
        Line line = getLineById(id);
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public Long addLineStationByName(Long id, LineStationCreateByNameRequest request) {
        String preStationName = request.getPreStationName();
        String stationName = request.getStationName();
        int distance = request.getDistance();
        int duration = request.getDuration();
        Long preStationId = stationRepository.findIdByName(preStationName);
        Long stationId = stationRepository.findIdByName(stationName);

        addLineStation(id, new LineStation(preStationId, stationId, distance, duration));
        return stationId;
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = getLineById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = getLineById(id);
        return LineResponse.of(line, findStationsOf(line));
    }

    public Set<Station> findStationsOf(Long id) {
        Line line = getLineById(id);
        return findStationsOf(line);
    }

    private Set<Station> findStationsOf(Line line) {
        return stationRepository.findAllById(line.getLineStationsId());
    }

    private Line getLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
    }
}
