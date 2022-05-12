package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.IllegalSectionDeleteException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SectionService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(final StationDao stationDao, final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public void createFirstInLine(final Long id, final Section section) {
        sectionDao.deleteAllByLine(id);
        sectionDao.save(id, section);
    }

    public void create(final Long id, final Long upStationId, final Long downStationId, final int distance) {
        final Sections sections = new Sections(sectionDao.findSectionsByLineId(id));
        final Section section = new Section(upStationId, downStationId, distance);
        sections.validatePossibleSection(section);
        insertSections(id, sections, section);
    }

    private void insertSections(Long id, Sections sections, Section section) {
        whenLastStation(id, sections, section);
        whenMatchUpStation(id, sections, section);
        whenMatchDownStation(id, sections, section);
    }

    private void whenMatchDownStation(Long id, Sections sections, Section section) {
        if (sections.matchDownStationId(section)) {
            final Section targetSection = sectionDao.findSectionByDownStationId(id, section);
            targetSection.validateDistanceLargerThan(section);
            insertSectionInTargetSection(id, targetSection.divideLeft(section), section);
        }
    }

    private void whenMatchUpStation(Long id, Sections sections, Section section) {
        if (sections.matchUpStationId(section)) {
            final Section targetSection = sectionDao.findSectionByUpStationId(id, section);
            targetSection.validateDistanceLargerThan(section);
            insertSectionInTargetSection(id, section, targetSection.divideRight(section));
        }
    }

    private void whenLastStation(Long id, Sections sections, Section section) {
        if (sections.isLastStation(section)) {
            sectionDao.save(id, section);
        }
    }

    private void insertSectionInTargetSection(final Long id, final Section leftSection, final Section rightSection) {
        sectionDao.editByUpStationId(id, leftSection);
        sectionDao.save(id, rightSection);
    }

    public List<Station> findStationsByLineId(final Long lineId) {
        return stationDao.findStationsById(findStationIdsByLineId(lineId));
    }

    private Set<Long> findStationIdsByLineId(final Long id) {
        final Set<Long> stationIds = new HashSet<>();
        for (final Section section : sectionDao.findSectionsByLineId(id)) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        return stationIds;
    }

    public void deleteSectionByStationId(final Long id, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findSectionsByStationId(id, stationId));
        if (sectionDao.countsByLine(id) == 1) {
            throw new IllegalSectionDeleteException();
        }
        if (sections.hasOneSection()) {
            sectionDao.deleteSectionByStationId(id, stationId);
        }
        if (sections.hasTwoSection()) {
            sectionDao.integrateSectionByStationId(id, stationId, sections.integrateTwoSections());
        }
    }
}
