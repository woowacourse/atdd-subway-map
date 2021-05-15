package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LinesResponse;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.SectionRepository;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import java.util.*;
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

    // TODO : 일급컬렉션을 활용하도록 변경
    public LineResponse getLineResponseByLineId(final Long id) {
        Line line = lineRepository.getLineById(id);
        List<StationResponse> allStations = stationService.getAllStations();
        List<StationResponse> stationResponsesByLineId = new ArrayList<>();

        for (Long stationId : getStationIdsByLineId(id)) {
            stationResponsesByLineId.add(allStations.stream()
                    .filter(stationResponse -> stationResponse.getId().equals(stationId))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new));
        }
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponsesByLineId);
    }

    // TODO : stations이 sections로부터 List<Section>을 받아서 처리할 작업
    private List<Long> getStationIdsByLineId(final Long id) {
        List<Section> sections = sectionRepository.getSectionsByLineId(id);
        Map<Long, Long> connectMap = new HashMap<>();

        for (Section section : sections) {
            connectMap.put(section.getUpStationId(), section.getDownStationId());
        }
        Long curId = getFrontId(connectMap);
        return getSortedStationIds(connectMap, curId);
    }

    // TODO : stations이 sections로부터 List<Section>을 받아서 처리할 작업
    private List<Long> getSortedStationIds(final Map<Long, Long> connectMap, Long curId) {
        List<Long> stationIdsByLineId = new ArrayList<>();
        stationIdsByLineId.add(curId);
        while (connectMap.containsKey(curId)) {
            curId = connectMap.get(curId);
            stationIdsByLineId.add(curId);
        }
        return stationIdsByLineId;
    }

    //TODO : Stations 이 FrontStation을 찾기 위해 필요한 과정
    private Long getFrontId(final Map<Long, Long> connectMap) {
        List<Long> keys = new ArrayList<>(connectMap.keySet());
        List<Long> values = new ArrayList<>(connectMap.values());

        return keys.stream().filter(key -> !values.contains(key)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getColor(), lineRequest.getName()));
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }
}
