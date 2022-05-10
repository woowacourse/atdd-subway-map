package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.exception.duplicate.DuplicateLineException;
import wooteco.subway.exception.notfound.NotFoundLineException;
import wooteco.subway.exception.notfound.NotFoundStationException;

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

    public LineResponse create(final CreateLineRequest request) {
        try {
            final Long lineId = lineDao.save(request.toLine());
            sectionDao.save(request.toSection(lineId));
            return show(lineId);
        } catch (final DuplicateKeyException e) {
            throw new DuplicateLineException();
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAll() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(this::findStations)
                .collect(Collectors.toList());
    }

    private LineResponse findStations(final Line line) {
        final Sections sections = sectionDao.findAllByLineId(line.getId());
        final List<Station> stations = sections.toSortedStationIds()
                .stream()
                .map(stationDao::findById)
                .collect(Collectors.toList());
        return LineResponse.of(line, stations);
    }

    @Transactional(readOnly = true)
    public LineResponse show(final Long id) {
        final Line line = lineDao.find(id);
        return findStations(line);
    }

    public void updateLine(final Long id, final UpdateLineRequest request) {
        validateNotExistLine(id);
        lineDao.update(id, request.getName(), request.getColor());
    }

    public void deleteLine(final Long id) {
        validateNotExistLine(id);
        lineDao.delete(id);
    }

    public void createSection(final Long lineId, final CreateSectionRequest request) {
        validateCreateSection(lineId, request);
        final Sections sections = sectionDao.findAllByLineId(lineId);
        sections.add(request.toSection(lineId));
        updateSections(lineId, sections);
    }

    private void validateCreateSection(final Long lineId, final CreateSectionRequest request) {
        validateNotExistLine(lineId);
        validateNotExistStation(request.getUpStationId());
        validateNotExistStation(request.getDownStationId());
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateDeleteSection(lineId, stationId);
        final Sections sections = sectionDao.findAllByLineId(lineId);
        sections.remove(stationId);
        updateSections(lineId, sections);
    }

    private void validateDeleteSection(Long lineId, Long stationId) {
        validateNotExistLine(lineId);
        validateNotExistStation(stationId);
    }

    private void validateNotExistLine(final Long id) {
        if (!lineDao.existsById(id)) {
            throw new NotFoundLineException();
        }
    }

    private void validateNotExistStation(final Long id) {
        if (!stationDao.existsById(id)) {
            throw new NotFoundStationException();
        }
    }

    private void updateSections(final Long lineId, final Sections sections) {
        sectionDao.deleteAllByLineId(lineId);
        for (Section section : sections.getSections()) {
            sectionDao.save(section);
        }
    }
}
