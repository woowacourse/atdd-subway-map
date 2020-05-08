package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
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

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line showLine(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
        line.addLineStation(request.toEntity());
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));

        line.removeLineStationById(stationId);
    }

    public LineResponse findLineWithStationsById(Long stationId) {
        Line line = lineRepository.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
        List<Station> stations = stationRepository.findAllById(line.getLineStationsId());

        return LineResponse.of(line, stations);
    }
}