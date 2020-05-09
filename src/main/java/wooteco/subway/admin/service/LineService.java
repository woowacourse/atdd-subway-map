package wooteco.subway.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.LineRequest;
import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
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

    public LineResponse save(Line line) {
        try {
            return LineResponse.of(lineRepository.save(line));
        } catch (Exception e) {
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
        return stationRepository.findAllById(line.getLineStationsId());
    }

    public LineResponse showLine(Long id) {
        Line line = findById(id);
        List<Station> stations = stationRepository.findAllById(line.getLineStationsId());

        return LineResponse.of(line, stations);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(lineRequest.toLine());
        try {
            lineRepository.save(line);
            List<Station> stations = stationRepository.findAllById(line.getLineStationsId());
            return LineResponse.of(line, stations);
        } catch (Exception e) {
            throw new IllegalArgumentException("중복된 노선 이름은 허용되지 않습니다.");
        }
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findById(id);

        line.addLineStation(request.toEntity());
        lineRepository.save(line);
        List<Station> stations = findStations(line);
        return LineResponse.of(line, stations);
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
        List<Station> stations = stationRepository.findAllById(line.getLineStationsId());

        return LineResponse.of(line, stations);
    }
}