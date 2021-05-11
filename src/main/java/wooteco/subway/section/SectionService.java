package wooteco.subway.section;

import org.springframework.stereotype.Service;
import wooteco.subway.section.exception.SectionDistanceException;
import wooteco.subway.section.exception.SectionInclusionException;
import wooteco.subway.section.exception.SectionNotFoundException;

@Service
public class SectionService {
    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section initialize(SectionDto sectionDto) {
        return sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                sectionDto.getDownStationId(), sectionDto.getDistance());
    }

    public Section save(SectionDto sectionDto) {
        validateSectionInclusion(sectionDto);
        if (hasEndPointInSection(sectionDto)) {
            return sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(),
                    sectionDto.getDownStationId(), sectionDto.getDistance());
        }
        return saveWithForkCase(sectionDto);
    }

    public void delete(Long lindId, Long stationId) {

    }

    private void validateSectionInclusion(SectionDto sectionDto) {
        if (hasBothStations(sectionDto.getLineId(), sectionDto.getUpStationId(), sectionDto.getDownStationId()) ||
                hasNeitherStations(sectionDto.getLineId(), sectionDto.getUpStationId(), sectionDto.getDownStationId())) {
            throw new SectionInclusionException();
        }
    }

    private boolean hasBothStations(Long lineId, Long upStationId, Long downStationId) {
        return sectionDao.isExistingStation(lineId, upStationId) &&
                sectionDao.isExistingStation(lineId, downStationId);
    }

    private boolean hasNeitherStations(Long lineId, Long upStationId, Long downStationId) {
        return !sectionDao.isExistingStation(lineId, upStationId) &&
                !sectionDao.isExistingStation(lineId, downStationId);
    }

    private boolean hasEndPointInSection(SectionDto sectionDto) {
        return sectionDao.hasEndPointInSection(sectionDto.getLineId(),
                sectionDto.getUpStationId(), sectionDto.getDownStationId());
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
             return sectionDao.findSectionByUpStationId(sectionDto.getLineId(), sectionDto.getUpStationId())
                     .orElseThrow(SectionNotFoundException::new);
        }
        return sectionDao.findSectionByDownStationId(sectionDto.getLineId(), sectionDto.getDownStationId())
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
