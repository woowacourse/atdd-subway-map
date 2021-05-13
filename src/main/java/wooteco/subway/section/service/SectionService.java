package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.NoSuchStationException;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(final SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Long save(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        Section section = new Section(
                lineId,
                new Station(upStationId),
                new Station(downStationId),
                distance);
        if (sectionRepository.isInitialSave(section)) {
            return sectionRepository.save(section);
        }
        validate(section);
        return add(section);
    }

    private void validate(final Section section) {
        if (bothStationsExist(section)) {
            throw new DuplicateSectionException();
        }
        if (bothStationsDoNotExist(section)) {
            throw new NoSuchStationException();
        }
    }

    private Long add(final Section section) {
        if (isNotEndStationSave(section)) {
            Section originalSection = sectionRepository.findByBaseStation(section);
            Section adjustedSection = originalSection.adjustBy(section);

            sectionRepository.update(adjustedSection);
        }
        return sectionRepository.save(section);
    }

    private boolean bothStationsExist(final Section section) {
        return sectionRepository.doesStationExist(section.getLineId(), section.getUpStationId()) &&
                sectionRepository.doesStationExist(section.getLineId(), section.getDownStationId());
    }

    private boolean bothStationsDoNotExist(final Section section) {
        return !sectionRepository.doesStationExist(section.getLineId(), section.getUpStationId()) &&
                !sectionRepository.doesStationExist(section.getLineId(), section.getDownStationId());
    }

    private boolean isNotEndStationSave(final Section section) {
        return !((sectionRepository.isEndStation(section.getLineId(), section.getDownStationId()) &&
                sectionRepository.doesExistInUpStation(section.getLineId(), section.getDownStationId())) ||
                (sectionRepository.isEndStation(section.getLineId(), section.getUpStationId()) &&
                        sectionRepository.doesExistInDownStation(section.getLineId(), section.getUpStationId())));
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateStationExistence(lineId, stationId);
        validateSectionCount(lineId);

        if (!sectionRepository.isEndStation(lineId, stationId)) {
            Section newSection = createNewSection(lineId, stationId);
            sectionRepository.save(newSection);
        }
        sectionRepository.deleteByStationId(lineId, stationId);
    }

    private void validateStationExistence(final Long lineId, final Long stationId) {
        if (!sectionRepository.doesStationExist(lineId, stationId)) {
            throw new NoSuchStationException();
        }
    }

    private void validateSectionCount(final Long lineId) {
        if (sectionRepository.isUnableToDelete(lineId)) {
            throw new UnavailableSectionDeleteException();
        }
    }

    private Section createNewSection(final Long lineId, final Long stationId) {
        long newUpStationId = sectionRepository.getNewUpStationId(lineId, stationId);
        long newDownStationId = sectionRepository.getNewDownStationId(lineId, stationId);
        int newDistance = sectionRepository.getNewDistance(lineId, stationId);

        return new Section(
                lineId,
                new Station(newUpStationId),
                new Station(newDownStationId),
                newDistance);
    }

    public Sections getAllSections(final Long lineId) {
        return sectionRepository.findAllSections(lineId);
    }
}
