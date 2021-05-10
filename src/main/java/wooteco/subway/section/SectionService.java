package wooteco.subway.section;

import org.springframework.stereotype.Service;
import wooteco.subway.section.exception.SectionDistanceException;
import wooteco.subway.section.exception.SectionInclusionException;

@Service
public class SectionService {
    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(SectionDto sectionDto) {
        validateSectionInclusion(sectionDto);
        if (isStationEndPoint(sectionDto)) {
            return sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                    sectionDto.getDownStationId(), sectionDto.getDistance());
        }
        return saveWithForkCase(sectionDto);
    }

    private void validateSectionInclusion(SectionDto sectionDto) {
        if (hasBothStations(sectionDto.getUpStationId(), sectionDto.getDownStationId()) &&
                hasNeitherStations(sectionDto.getUpStationId(), sectionDto.getDownStationId())) {
            throw new SectionInclusionException();
        }
    }

    private boolean hasBothStations(Long upStationId, Long downStationId) {
        return sectionDao.isExistingStation(upStationId) &&
                sectionDao.isExistingStation(downStationId);
    }

    private boolean hasNeitherStations(Long upStationId, Long downStationId) {
        return !sectionDao.isExistingStation(upStationId) &&
                !sectionDao.isExistingStation(downStationId);
    }

    private boolean isStationEndPoint(SectionDto sectionDto) {
        return sectionDao.isStationEndPoint(sectionDto.getUpStationId(), sectionDto.getDownStationId());
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
             return sectionDao.findSectionByUpStationId(sectionDto.getUpStationId());
        }
        return sectionDao.findSectionByDownStationId(sectionDto.getDownStationId());
    }

    private void validateSectionDistance(SectionDto sectionDto, Section findSection) {
        if (sectionDto.getDistance() >= findSection.getDistance()) {
            throw new SectionDistanceException();
        }
    }

    private SectionStandard calculateSectionStandard(SectionDto sectionDto) {
        if (sectionDao.isExistingStation(sectionDto.getUpStationId())) {
            return SectionStandard.FROM_UP_STATION;
        }
        return SectionStandard.FROM_DOWN_STATION;
    }
}
