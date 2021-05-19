package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.exception.SectionInitializationException;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section initialize(SectionDto sectionDto) {
        return sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                sectionDto.getDownStationId(), sectionDto.getDistance());
    }

    @Transactional
    public Section save(SectionDto sectionDto) {
        validateSectionInitialization(sectionDto.getLineId());
        Sections sections = new Sections(sectionDao.findAllByLineId(sectionDto.getLineId()));
        sections.validateSectionInclusion(sectionDto);

        if (sections.canAttachAfterEndStation(sectionDto.getUpStationId(), sectionDto.getDownStationId())) {
            return sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                    sectionDto.getDownStationId(), sectionDto.getDistance());
        }
        return saveWithForkCase(sectionDto, sections);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        validateSectionInitialization(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateNumberOfStation();
        if (sections.isEndStation(stationId)) {
            sectionDao.delete(lineId, stationId);
            return;
        }
        deleteAndConnectSection(lineId, stationId, sections);
    }

    private void deleteAndConnectSection(Long lineId, Long stationId, Sections sections) {
        Section frontSection = sections.findSectionByDownStationId(stationId);
        Section backSection = sections.findSectionByUpStationId(stationId);

        sectionDao.updateDistanceAndDownStation(
                lineId, frontSection.getUpStationId(), backSection.getDownStationId(),
                frontSection.getDistance() + backSection.getDistance()
        );
        sectionDao.delete(backSection.getId());
    }

    private void validateSectionInitialization(Long lineId) {
        if (sectionDao.numberOfEnrolledSection(lineId) == 0) {
            throw new SectionInitializationException();
        }
    }

    private Section saveWithForkCase(SectionDto sectionDto, Sections sections) {
        SectionStandard sectionStandard = sections.calculateSectionStandard(sectionDto);
        Section findSection = findSectionWithExistingStation(sectionStandard, sections, sectionDto);
        findSection.validateSectionDistance(sectionDto);

        sectionDao.delete(findSection.getId());
        Section savedSection = sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                sectionDto.getDownStationId(), sectionDto.getDistance());

        if (sectionStandard == SectionStandard.FROM_UP_STATION) {
            sectionDao.save(sectionDto.getLineId(), sectionDto.getDownStationId(),
                    findSection.getDownStationId(), findSection.getDistance() - sectionDto.getDistance());
            return savedSection;
        }
        sectionDao.save(sectionDto.getLineId(), findSection.getUpStationId(),
                sectionDto.getUpStationId(), findSection.getDistance() - sectionDto.getDistance());

        return savedSection;
    }

    private Section findSectionWithExistingStation(SectionStandard sectionStandard, Sections sections, SectionDto sectionDto) {
        if (sectionStandard == SectionStandard.FROM_UP_STATION) {
            return sections.findSectionByUpStationId(sectionDto.getUpStationId());
        }
        return sections.findSectionByDownStationId(sectionDto.getDownStationId());
    }
}
