package wooteco.subway.admin.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.LineRequest;
import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        try {
            return LineResponse.of(lineRepository.save(lineRequest.toLine()));
        } catch (DbActionExecutionException e) {
            throwDuplicateKeyException(e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void throwDuplicateKeyException(DbActionExecutionException e) {
        if (e.getCause() instanceof DuplicateKeyException) {
            throw new IllegalArgumentException("중복된 노선 이름은 허용되지 않습니다.");
        }
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            List<Station> stations = findStations(line);
            lineResponses.add(LineResponse.of(line, stations));
        }
        return lineResponses;
    }

    private List<Station> findStations(Line line) {
        return stationRepository.findAllByLineId(line.getId());
    }

    public LineResponse showLine(Long id) {
        Line line = findById(id);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(lineRequest.toLine());
        try {
            lineRepository.save(line);
        } catch (DbActionExecutionException e) {
            throwDuplicateKeyException(e);
            throw new IllegalArgumentException(e.getMessage());
        }
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationCreateRequest request) {
        LineStation lineStation = request.toLineStation();
        validateStationExist(lineStation);

        Line line = findById(id);
        line.addLineStation(lineStation);
        lineRepository.save(line);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    private void validateStationExist(LineStation lineStation) {
        if (lineStation.isStationIdNull() || !stationRepository.findById(lineStation.getStationId()).isPresent()) {
            throw new IllegalArgumentException("추가하려는 지하철 역이 존재하지 않습니다.");
        }
        if (!lineStation.isPreStationIdNull() && !stationRepository.findById(lineStation.getPreStationId()).isPresent()) {
            throw new IllegalArgumentException("추가하려는 전 역이 존재하지 않습니다.");
        }
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long stationId) {
        Line line = findById(stationId);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }
}