package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.SectionDao;
import wooteco.subway.line.section.SectionRequest;
import wooteco.subway.line.section.SectionResponse;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.StationService;
import wooteco.subway.station.Stations;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public List<LineResponse> findLines() {
        return lineDao.findAll().
            stream().
            map(line -> findLine(line.getId())).
            collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        final Line line = composeLine(id);
        return LineResponse.from(line, composeStations(line.getSections()));
    }

    private Line composeLine(final Long lineId) {
        final Line line = lineDao.findById(lineId)
            .orElseThrow(() -> new DataNotFoundException("해당 Id의 노선이 없습니다."));
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        return new Line(line.getId(), line.getName(), line.getColor(), sections);
    }

    private Stations composeStations(final Sections sections) {
        return stationService.findByIds(sections.distinctStationIds());
    }

    @Transactional
    public LineResponse createLine(final LineRequest lineRequest) {
        final Line line = lineDao.save(lineRequest.toEntity());
        line.validateDifferentStationIds(lineRequest.getUpStationId(), lineRequest.getDownStationId());
        sectionDao.save(lineRequest.toSectionEntity(line.getId()));
        return findLine(line.getId());
    }

    @Transactional
    public SectionResponse addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Line line = composeLine(lineId);
        final Long upStationId = sectionRequest.getUpStationId();
        final Long downStationId = sectionRequest.getDownStationId();

        line.validateStationsToAddSection(upStationId, downStationId);

        if (line.includesTerminalStation(upStationId, downStationId)) {
            return createSection(sectionRequest.toEntity(lineId));
        }

        final int distance = sectionRequest.getDistance();
        final Section targetSection = line.findUpdatedTarget(upStationId, downStationId, distance);
        final Section updatedSection = targetSection.createUpdatedSection(upStationId, downStationId, distance);
        sectionDao.update(updatedSection);
        return createSection(sectionRequest.toEntity(lineId));
    }

    private SectionResponse createSection(final Section section) {
        return SectionResponse.from(sectionDao.save(section));
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(final Long id) {
        lineDao.deleteById(id);
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = composeLine(lineId);
        line.validateSizeToDeleteSection();
        if (line.isTerminalStation(stationId)) {
            sectionDao.deleteById(line.findTerminalSection(stationId).getId());
            return;
        }
        deleteMiddleStation(stationId, line);
    }

    private void deleteMiddleStation(final Long stationId, final Line line) {
        final Section leftSection = line.findSectionHasDownStation(stationId);
        final Section rightSection = line.findSectionHasUpStation(stationId);
        final Section connectedSection = line.createConnectedSection(leftSection, rightSection);

        sectionDao.deleteById(leftSection.getId());
        sectionDao.deleteById(rightSection.getId());
        sectionDao.save(connectedSection);
    }
}
