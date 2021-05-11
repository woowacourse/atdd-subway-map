package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LinesResponse;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.repository.SectionRepository;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final StationService stationService;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public LineService(final StationService stationService, final LineRepository lineRepository, final SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public List<LinesResponse> getAllLines() {
        List<Line> lines = lineRepository.getLines();
        return lines.stream()
                .map(it -> new LinesResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getColor(), lineRequest.getName());
        if (lineRepository.isExistName(newLine)) {
            throw new DuplicateException("이미 존재하는 Line 입니다.");
        }
        validateStationIds(lineRequest);
        Line savedLine = lineRepository.save(newLine);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
    }

    private void validateStationIds(final LineRequest lineRequest) {
        if (lineRequest.getDownStationId().equals(lineRequest.getUpStationId())) {
            throw new IllegalArgumentException("새 노선 등록시, 상행선과 하행선이 같을 수 없습니다.");
        }
    }

    public LineResponse getLineResponseById(final Long id) {
        Line line = lineRepository.getLineById(id);
        List<Long> sortedStationIds = getStationIdsById(id);

        List<StationResponse> stationResponses = stationService.getAllStations();
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                stationResponses.stream()
                        .filter(stationResponse -> sortedStationIds.contains(stationResponse.getId()))
                        .collect(Collectors.toList())
        );
    }

    private List<Long> getStationIdsById(final Long id) {
        List<Section> sections = sectionRepository.getSectionsByLineId(id);
        Map<Long, Long> connectMap = new HashMap<>();

        for (Section section : sections) {
            connectMap.put(section.getUpStationId(), section.getDownStationId());
        }
        Long curId = getFrontId(connectMap);
        return getSortedStationIds(connectMap, curId);
    }

    private List<Long> getSortedStationIds(final Map<Long, Long> connectMap, Long curId) {
        List<Long> stationIdsByLineId = new ArrayList<>();
        stationIdsByLineId.add(curId);
        while (connectMap.containsKey(curId)) {
            curId = connectMap.get(curId);
            stationIdsByLineId.add(curId);
        }
        return stationIdsByLineId;
    }

    private Long getFrontId(final Map<Long, Long> connectMap) {
        List<Long> keys = new ArrayList<>(connectMap.keySet());
        List<Long> values = new ArrayList<>(connectMap.values());

        return keys.stream().filter(key -> !values.contains(key)).findFirst().orElse(null);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getColor(), lineRequest.getName()));
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }
}
