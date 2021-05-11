package wooteco.subway.section;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.section.NotEnoughSectionException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Transactional
    public Section create(Section section, Long lineId) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        Optional<Section> affectedSection = sections.affectedSection(section);

        sections.add(section);

        return sectionDao.saveAffectedSections(section, affectedSection, lineId);
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        validateExistLine(lineId);
        validateExistStation(stationId);
        validateIsLastSection(lineId);

        List<Section> sections = sectionDao.findSectionContainsStationId(lineId, stationId);
        final Sections foundSections = Sections.from(sections);
        Optional<Section> affectedSection = foundSections.transformSection(stationId);

        sectionDao.removeSections(lineId, sections);
        affectedSection.ifPresent(section -> sectionDao.insertSection(section, lineId));
    }

    private void validateIsLastSection(Long lineId) {
        if (sectionDao.findSectionsByLineId(lineId).hasSize(1)) {
            throw new NotEnoughSectionException();
        }
    }

    private void validateExistStation(Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new StationNotFoundException();
        }
    }

    private void validateExistLine(Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new LineNotFoundException();
        }
    }
}
