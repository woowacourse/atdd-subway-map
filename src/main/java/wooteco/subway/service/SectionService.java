package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.section.request.SectionInsertRequest;
import wooteco.subway.dto.section.response.SectionInsertResponse;
import wooteco.subway.exception.section.SectionDistanceException;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void create(Section section) {
        sectionDao.insert(section);
    }

    public SectionInsertResponse add(Long lineId, SectionInsertRequest sectionInsertRequest) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section newSection = sectionInsertRequest.toEntity(lineId);

        sections.validateAddableNewStation(newSection);

        if (sections.isExistInUpStationIds(newSection.getUpStationId())) {
            updateUpStationId(lineId, sections, newSection);
        }

        if (sections.isExistInDownStationIds(newSection.getDownStationId())) {
            updateDownStationId(lineId, sections, newSection);
        }
        return new SectionInsertResponse(sectionDao.insert(newSection));
    }

    private void updateUpStationId(Long lineId, Sections sections, Section newSection) {
        Section previousSection = sections.getPreviousSection(newSection);
        validateDistance(newSection, previousSection);
        int updatedPreviousDistance = previousSection.minusDistance(newSection);
        sectionDao.updateUpStationId(
                newSection.getDownStationId(),
                updatedPreviousDistance,
                previousSection.getUpStationId(),
                previousSection.getDownStationId(),
                lineId);
    }

    private void updateDownStationId(Long lineId, Sections sections, Section newSection) {
        Section followingSection = sections.getFollowingSection(newSection);
        validateDistance(newSection, followingSection);
        int updatedFollowingDistance = followingSection.minusDistance(newSection);
        sectionDao.updateDownStationId(
                newSection.getUpStationId(),
                updatedFollowingDistance,
                followingSection.getUpStationId(),
                followingSection.getDownStationId(),
                lineId);
    }

    private void validateDistance(Section newSection, Section previousSection) {
        if (newSection.isGreaterOrEqualThan(previousSection)) {
            throw new SectionDistanceException(newSection, previousSection);
        }
    }

    public Sections findAllByLineId(Long lineId) {
        return new Sections(sectionDao.findAllByLineId(lineId));
    }

    public void deleteById(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateSectionSize(lineId);

        if (sections.isFirstOrLastStation(stationId)) {
            deleteFirstOrLastStation(sections, stationId);
            return;
        }
        deleteStationFromMiddleOfLine(lineId, stationId, sections);
    }

    private void deleteFirstOrLastStation(Sections sections, Long stationId) {
        Long sectionId = sections.getSectionIdToDelete(stationId);
        sectionDao.deleteById(sectionId);
    }

    private void deleteStationFromMiddleOfLine(Long lineId, Long stationId, Sections sections) {
        Long UpStationSectionId = sections.getUpStationSectionId(stationId);
        Long DownStationSectionId = sections.getDownStationSectionId(stationId);
        Section newSection = sections.getNewSection(lineId, stationId);
        sectionDao.deleteById(UpStationSectionId);
        sectionDao.deleteById(DownStationSectionId);
        sectionDao.insert(newSection);
    }

    public boolean existStationByStationId(Long stationId) {
        return sectionDao.findByStationId(stationId)
                .isPresent();
    }
}
