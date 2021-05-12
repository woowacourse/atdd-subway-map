package wooteco.subway.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        Station upStation = stationService.showStation(lineRequest.getUpStationId()).toDomain();
        Station downStation = stationService.showStation(lineRequest.getDownStationId()).toDomain();
        Section section = new Section(upStation, downStation,
            new Distance(lineRequest.getDistance()));

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), new Sections(section));
        LineEntity lineEntity = lineDao.save(line);
        Section newSection = sectionService.createSection(section, lineEntity.getId());
        return new LineResponse(subwayMapper.line(lineEntity, new Sections(newSection)));
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
            .map(lineEntity -> new LineResponse(lineFromEntity(lineEntity)))
            .collect(Collectors.toList());
    }

    private Line lineFromEntity(LineEntity lineEntity) {
        Set<Section> sections = sectionService.findSectionsByLineId(lineEntity.getId());
        return subwayMapper.line(lineEntity, new Sections(sections));
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        validateToExistId(id);
        Line line = lineFromEntity(lineDao.findById(id));

        return new LineResponse(line);
    }

    private void validateToExistId(Long id) {
        if (!lineDao.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 노선 ID입니다.");
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
        if (lineDao.existsByNameAndColorExceptId(id, name, color)) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름 또는 색깔입니다.");
        }
    }

    public void deleteLine(Long id) {
        validateToExistId(id);
        lineDao.deleteById(id);
    }

    public void createSection(Long id, SectionRequest sectionRequest) {
        validateToExistId(id);
        Line line = subwayMapper.line(lineDao.findById(id), new Sections(
            sectionService.findSectionsByLineId(id)));
        Station upStation = stationService.showStation(sectionRequest.getUpStationId()).toDomain();
        Station downStation = stationService.showStation(sectionRequest.getDownStationId())
            .toDomain();
        Section section = new Section(upStation, downStation,
            new Distance(sectionRequest.getDistance()));
        sectionService.createSectionInLine(section, line);
    }

    public void deleteSections(Long id, Long stationId) {
        validateToExistId(id);
        Line line = subwayMapper.line(lineDao.findById(id), new Sections(
            sectionService.findSectionsByLineId(id)));
        Station station = stationService.showStation(stationId).toDomain();
        sectionService.removeStationInLine(station, line);
    }
}
