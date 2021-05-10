package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Construction;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domainmapper.SubwayMapper;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.repository.LineDao;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;
    private final SubwayMapper subwayMapper;

    public LineService(LineDao lineDao, SectionService sectionService,
        StationService stationService, SubwayMapper subwayMapper) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
        this.subwayMapper = subwayMapper;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        validateNameAndColor(lineRequest);

        Line line = subwayMapper
            .line(lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor())));
        Station upStation = stationService.showStation(lineRequest.getUpStationId()).toDomain();
        Station downStation = stationService.showStation(lineRequest.getDownStationId()).toDomain();
        Section newSection = sectionService.createSection(
            new Section(line, upStation, downStation, new Distance(lineRequest.getDistance())));

        return new LineResponse(line, newSection);
    }

    private void validateNameAndColor(LineRequest lineRequest) {
        if (lineDao.existsByNameOrColor(lineRequest.getName(), lineRequest.getColor())) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름 또는 색깔입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        return lineEntities.stream()
            .map(lineEntity -> new LineResponse(lineEntity.getId(), lineEntity.getName(),
                lineEntity.getColor()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        validateToExistId(id);
        Line line = subwayMapper.line(lineDao.findById(id));
        Sections sections = new Sections(sectionService.findSectionsByLine(line));

        return new LineResponse(line, sections.pathByLine(line));
    }

    private void validateToExistId(Long id) {
        if (!lineDao.hasLineWithId(id)) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        validateToUpdateLine(id, lineRequest);
        lineDao.updateById(id, new LineEntity(id, lineRequest.getName(), lineRequest.getColor()));
    }

    private void validateToUpdateLine(Long id, LineRequest lineRequest) {
        validateToExistId(id);
        validateNotToDuplicateNameAndColor(id, lineRequest.getName(), lineRequest.getColor());
    }

    private void validateNotToDuplicateNameAndColor(Long id, String name, String color) {
        if (lineDao.hasLineWithNameAndColorWithoutId(id, name, color)) {
            throw new IllegalArgumentException("이미 존재하는 이름 또는 색깔입니다.");
        }
    }

    public void deleteLine(Long id) {
        validateToExistId(id);
        lineDao.deleteById(id);
    }

    public void createSection(Long id, SectionRequest sectionRequest) {
        validateToExistId(id);
        Line line = subwayMapper.line(lineDao.findById(id));
        Station upStation = stationService.showStation(sectionRequest.getUpStationId()).toDomain();
        Station downStation = stationService.showStation(sectionRequest.getDownStationId())
            .toDomain();
        Section section = new Section(line, upStation, downStation,
            new Distance(sectionRequest.getDistance()));
        insertSection(line, section);
    }

    private void insertSection(Line line, Section section) {
        Sections sections = new Sections(sectionService.findSectionsByLine(line));
        Construction construction = sections.construction(line);
        construction.insertSection(section);
        updateSections(construction);
    }

    private void updateSections(Construction construction) {
        for (Section sectionToCreate : construction.sectionsToCreate()) {
            sectionService.createSection(sectionToCreate);
        }
        for (Section sectionToRemove : construction.getSectionsToRemove()) {
            sectionService.remove(sectionToRemove.getId());
        }
    }

    public void deleteSections(Long lineId, Long stationId) {
        validateToExistId(lineId);
        Line line = subwayMapper.line(lineDao.findById(lineId));
        Sections sections = new Sections(sectionService.findSectionsByLine(line));
        Station station = stationService.showStation(stationId).toDomain();
        removeStationInLine(line, sections, station);
    }

    private void removeStationInLine(Line line, Sections sections, Station station) {
        Construction construction = sections.construction(line);
        construction.deleteSectionsByStation(station);
        updateSections(construction);
    }
}
