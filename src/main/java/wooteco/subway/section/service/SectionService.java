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
    public Section save(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        Sections sections = sectionDao.findAllByLineId(lineId);
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

    private Section add(final Sections sections, final Section sectionToSave) {
        if (sections.isNotEndStationSave(sectionToSave)) {
            Section originalSection = sections.findOriginalSection(sectionToSave);
            Section adjustedSection = originalSection.adjustBy(sectionToSave);

            sectionDao.update(adjustedSection);
        }
        return sectionDao.save(sectionToSave);
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        Station stationToDelete = new Station(stationId);

        validateDeleteAvailability(sections);
        validateStationExistence(sections, stationToDelete);

        if (!sections.isEndStation(stationToDelete)) {
            Section newSection = sections.createNewSection(lineId, stationToDelete);
            sectionDao.save(newSection);
        }
        sectionDao.deleteByStationId(lineId, stationId);
    }

    private void validateDeleteAvailability(final Sections sections) {
        if (sections.isUnableToDelete()) {
            throw new UnavailableSectionDeleteException();
        }
    }

    private void validateStationExistence(final Sections sections, final Station station) {
        if (!sections.doesStationExist(station)) {
            throw new NoSuchStationException();
        }
    }

    public Sections findAllByLineId(final Long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }
}
