package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.controller.dto.LineNameColorResponse;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.controller.dto.SectionRequest;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.repository.LineRepository;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineNameColorResponse saveLine(final LineRequest lineRequest) {
        Line savedLine = lineRepository.saveLine(lineRequest.toLineEntity());
        addSectionByLineSave(savedLine, lineRequest);
        return LineNameColorResponse.from(savedLine);
    }

    private Section addSectionByLineSave(Line line, LineRequest lineRequest) {
        if (!lineRequest.getUpStationId().equals(lineRequest.getDownStationId())) {
            Section section = lineRequest.toSectionEntity(line.getId());
            return lineRepository.saveSection(section);
        }
        throw new IllegalArgumentException("구간의 상행역과 하행역은 같을 수 없습니다.");
    }

    @Transactional(readOnly = true)
    public List<LineNameColorResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(LineNameColorResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long id) {
        Line line = lineRepository.findLineSectionById(id);
        List<Long> sortedStationIds = line.sortingSectionIds();
        Stations stations = stationService.findSortStationsByIds(sortedStationIds);
        return LineResponse.from(line, stations);
    }

    @Transactional
    public void delete(final Long id) {
        lineRepository.delete(id);
    }

    @Transactional
    public void update(final Long lineId, final LineRequest lineRequest) {
        Line line = lineRepository.findLineSectionById(lineId);
        Line updatedLine = line.update(lineRequest.getName(), lineRequest.getColor());
        lineRepository.update(updatedLine);
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Section section = sectionRequest.toEntity(lineId);
        lineRepository.addSection(section);
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        lineRepository.deleteSection(lineId, stationId);
    }
}
