package wooteco.subway.admin.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
            lineResponses.add(LineResponse.convert(line, findStationsByLineId(line.getId())));
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
        // TODO: 구현
        Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        line.addLineStation(LineStationCreateRequest.toLineStation(request));
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
        Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long id) {
        // TODO: 구현
        final Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 노선이 존재하지 않습니다."));
        LineResponse lineResponse = LineResponse.of(line);
        final List<Long> lineStationIds = line.findLineStationsId();
        lineResponse.setStations(Sets.newHashSet(stationRepository.findAllById(lineStationIds)));
        return lineResponse;
    }

    public Set<Station> findStationsByLineId(Long lineId) {
        final Optional<Station> byId = stationRepository.findById(lineId);
        if (byId.equals(Optional.empty()))
            return Collections.EMPTY_SET;
        return Sets.newHashSet(stationRepository.findById(lineId).get());
    }

    public int findStationIdByName(String name) {
        return stationRepository.findIdByName(name);
    }
}
