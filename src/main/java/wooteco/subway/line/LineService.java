package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.line.exception.DuplicatedStationException;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.SectionDao;
import wooteco.subway.line.section.SectionRequest;
import wooteco.subway.line.section.SectionResponse;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationService;
import wooteco.subway.station.Stations;

@Transactional
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse createLine(final LineRequest lineRequest) {
        final Line line = lineDao.save(lineRequest.toEntity());

        final Long upStationId = lineRequest.getUpStationId();
        final Long downStationId = lineRequest.getDownStationId();
        final int distance = lineRequest.getDistance();

        createSection(line.getId(), new SectionRequest(upStationId, downStationId, distance));
        return LineResponse.from(line);
    }

    private SectionResponse createSection(final Long lineId, final SectionRequest sectionRequest) {
        validateDifferentStation(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        final Section section = sectionDao.save(sectionRequest.toEntity(lineId));
        return SectionResponse.from(section);
    }

    private void validateDifferentStation(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new DuplicatedStationException("상행과 하행역이 같습니다.");
        }
    }

    public SectionResponse addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Line line = composeLine(lineId);
        final Long upStationId = sectionRequest.getUpStationId();
        final Long downStationId = sectionRequest.getDownStationId();

        line.validateStationsToAddSection(upStationId, downStationId);

        if (line.isAddableTerminalStation(upStationId, downStationId)) {
            return createSection(lineId, sectionRequest);
        }

        final int distance = sectionRequest.getDistance();
        final Section targetSection = line.findUpdatedTarget(upStationId, downStationId, distance);
        final Section updatedSection = targetSection.createUpdatedSection(upStationId, downStationId, distance);
        sectionDao.update(updatedSection);
        return createSection(lineId, sectionRequest);
    }

    private Line composeLine(final Long lineId) {
        final Line line = lineDao.findById(lineId).orElseThrow(() ->
            new DataNotFoundException("해당 Id의 노선이 없습니다."));
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        final List<Station> stationsGroup = stationDao.findByIds(sections.distinctStationIds());
        return new Line(line.getId(), line.getName(), line.getColor(), sections, new Stations(stationsGroup));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        return lineDao.findAll().stream().
            map(LineResponse::from).
            collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        final Line line = composeLine(id);
        return LineResponse.from(line);
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(final Long id) {
        lineDao.deleteById(id);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = composeLine(lineId);
        line.validateSizeToDeleteSection();
        if (line.isTerminalStation(stationId)) {
            sectionDao.deleteById(line.findTerminalSection(stationId).getId());
            return;
        }
        deleteMiddleStation(lineId, stationId, line);
    }

    private void deleteMiddleStation(final Long lineId, final Long stationId, final Line line) {
        final Section leftSection = line.findSectionHasDownStation(stationId);
        final Section rightSection = line.findSectionHasUpStation(stationId);
        final Section newSection = Section.of(lineId, leftSection, rightSection);

        sectionDao.deleteById(leftSection.getId());
        sectionDao.deleteById(rightSection.getId());
        sectionDao.save(newSection);
    }
}
