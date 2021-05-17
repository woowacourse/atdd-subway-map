package wooteco.subway.section;

import org.springframework.stereotype.Service;
import wooteco.subway.section.exception.*;
import wooteco.subway.station.exception.StationNotFoundException;

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

    public Section save(SectionDto sectionDto) {
        validateSectionInitialization(sectionDto.getLineId());
        Sections sections = new Sections(sectionDao.findAllByLineId(sectionDto.getLineId()));
        sections.validateSectionInclusion(sectionDto);

        if (sections.attachesAfterEndStation(sectionDto)) {
            return sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                    sectionDto.getDownStationId(), sectionDto.getDistance());
        }
        return saveWithForkCase(sectionDto);
    }

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

    private Section saveWithForkCase(SectionDto sectionDto) {
        Section findSection = findSectionWithExistingStation(sectionDto);
        validateSectionDistance(sectionDto, findSection);

        sectionDao.delete(findSection.getId());
        Section savedSection = sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                sectionDto.getDownStationId(), sectionDto.getDistance());
        sectionDao.save(sectionDto.getLineId(), sectionDto.getDownStationId(),
                findSection.getDownStationId(), findSection.getDistance() - sectionDto.getDistance());
        return savedSection;
    }

    private Section findSectionWithExistingStation(SectionDto sectionDto) {
        SectionStandard sectionStandard = calculateSectionStandard(sectionDto);
        if (sectionStandard == SectionStandard.FROM_UP_STATION) {
            return sectionDao.findByUpStationId(sectionDto.getLineId(), sectionDto.getUpStationId())
                    .orElseThrow(SectionNotFoundException::new);
        }
        return sectionDao.findByDownStationId(sectionDto.getLineId(), sectionDto.getDownStationId())
                .orElseThrow(SectionNotFoundException::new);
    }

    private void validateSectionDistance(SectionDto sectionDto, Section findSection) {
        if (sectionDto.getDistance() >= findSection.getDistance()) {
            throw new SectionDistanceException();
        }
    }

    private SectionStandard calculateSectionStandard(SectionDto sectionDto) {
        if (sectionDao.isExistingStation(sectionDto.getLineId(), sectionDto.getUpStationId())) {
            return SectionStandard.FROM_UP_STATION;
        }
        return SectionStandard.FROM_DOWN_STATION;
    }
}
