package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.repository.SectionDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.NoSuchStationException;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Long save(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        Sections sections = sectionDao.findAllSections(lineId);
        Section sectionToSave = new Section(
                lineId,
                new Station(upStationId),
                new Station(downStationId),
                distance);

        if (sections.isEmpty()) {
            return sectionDao.save(sectionToSave);
        }

        validateSectionSave(sections, sectionToSave);
        return add(sections, sectionToSave);
    }

    private void validateSectionSave(final Sections sections, final Section section) {
        if (sections.bothStationsExist(section)) {
            throw new DuplicateSectionException();
        }
        if (sections.bothStationsDoNotExist(section)) {
            throw new NoSuchStationException();
        }
    }

    private long add(final Sections sections, final Section sectionToSave) {
        if (sections.isNotEndStationSave(sectionToSave)) {
            Section originalSection = sections.findOriginalSection(sectionToSave);
            Section adjustedSection = originalSection.adjustBy(sectionToSave);

            sectionDao.update(adjustedSection);
        }
        return sectionDao.save(sectionToSave);
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        Sections sections = sectionDao.findAllSections(lineId);
        Station stationToDelete = new Station(stationId);

        validateStationExistence(sections, stationToDelete);
        validateSectionCount(sections);

        if (!sections.isEndStation(stationToDelete)) {
            Section newSection = sections.createNewSection(lineId, stationToDelete);
            sectionDao.save(newSection);
        }
        sectionDao.deleteByStationId(lineId, stationId);
    }

    private void validateStationExistence(final Sections sections, final Station station) {
        if (!sections.doesStationExist(station)) {
            throw new NoSuchStationException();
        }
    }

    private void validateSectionCount(final Sections sections) {
        if (sections.isUnableToDelete()) {
            throw new UnavailableSectionDeleteException();
        }
    }

    public Sections findAll(final Long lineId) {
        return sectionDao.findAllSections(lineId);
    }
}
