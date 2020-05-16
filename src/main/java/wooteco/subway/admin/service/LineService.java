package wooteco.subway.admin.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
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

    public List<LineResponse> showLines() {
        List<LineResponse> lineResponses = new ArrayList<>();
        final List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            lineResponses.add(
                LineResponse.convert(line, findStationsByLineId(line.findLineStationsId())));
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

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        line.addLineStation(LineStationCreateRequest.toLineStation(request));
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        final Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 노선이 존재하지 않습니다."));
        return LineResponse.convert(line, findStationsByLineId(line.findLineStationsId()));
    }

    public Set<Station> findStationsByLineId(List<Long> stationIds) {
        return Sets.newHashSet(stationRepository.findAllById(stationIds));
    }
}
