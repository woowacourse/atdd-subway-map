package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.LineRequest;
import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest request) {
        try {
            return LineResponse.of(lineRepository.save(request.toLine()));
        } catch (Exception e) {
            throw new IllegalArgumentException("중복된 노선 이름은 허용되지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(line -> showLine(line.getId()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        Line line = findById(id);
        return lineResponse(line);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        try {
            Line line = findById(id);
            line.update(lineRequest.toLine());
            lineRepository.save(line);
            return lineResponse(line);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("중복된 노선 이름은 허용되지 않습니다.");
        }
    }

    private LineResponse lineResponse(Line line) {
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findById(id);
        line.addLineStation(request.toLineStation());
        lineRepository.save(line);
        return lineResponse(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineByStationId(Long stationId) {
        Line line = findById(stationId);
        return lineResponse(line);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

    private List<Station> findStations(Line line) {
        return stationRepository.findAllByLineId(line.getId());
    }
}