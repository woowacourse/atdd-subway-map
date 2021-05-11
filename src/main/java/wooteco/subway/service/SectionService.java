package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.section.request.SectionInsertRequest;
import wooteco.subway.dto.section.response.SectionInsertResponse;
import wooteco.subway.exception.section.SectionDistanceException;
import wooteco.subway.exception.section.SectionMiniMumDeleteException;
import wooteco.subway.dao.SectionDao;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionInsertResponse create(Long lineId, SectionInsertRequest sectionInsertRequest) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section newSection = sectionInsertRequest.toEntity(lineId);

        sections.validate(newSection);

        if (sections.isExistInUpStationIds(newSection.getUpStationId())) {
            Section previousSection = sections.getPreviousSection(newSection);
            validateDistanceAndUpdateUpStationId(lineId, newSection, previousSection);
        }
        if (sections.isExistInDownStationIds(newSection.getDownStationId())) {
            Section followingSection = sections.getFollowingSection(newSection);
            validateDistanceAndUpdateDownStationId(lineId, newSection, followingSection);
        }
        Section insertedSection = sectionDao.insert(newSection);
        return new SectionInsertResponse(insertedSection);
    }

    public void deleteSectionById(Long lineId, Long stationIdToDelete) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        if(sections.getSectionsSize() == 1){
            throw new SectionMiniMumDeleteException(lineId);
        }
        if (sections.isFirstOrLastStation(stationIdToDelete)) {
            deleteFirstOrLastStation(sections, stationIdToDelete);
            return;
        }
        deleteStationFromMiddleOfLine(lineId, stationIdToDelete, sections);
    }

    private void deleteFirstOrLastStation(Sections sections, Long stationIdToDelete) {
        Long sectionIdToDelete = sections.getSectionIdToDelete(stationIdToDelete);
        sectionDao.deleteById(sectionIdToDelete);
    }

    private void deleteStationFromMiddleOfLine(Long lineId, Long stationIdToDelete, Sections sections) {
        Long UpStationSectionId = sections.getUpStationSectionId(stationIdToDelete);
        Long DownStationSectionId = sections.getDownStationSectionId(stationIdToDelete);
        Section newSection = sections.getNewSection(lineId, stationIdToDelete);
        sectionDao.deleteById(UpStationSectionId);
        sectionDao.deleteById(DownStationSectionId);
        sectionDao.insert(newSection);
    }

    private void validateDistanceAndUpdateUpStationId(Long lineId, Section newSection, Section previousSection) {
        if (newSection.isShorterThan(previousSection)) {
            int updatedPreviousDistance = previousSection.getDistance() - newSection.getDistance();
            sectionDao.updateUpStationId(
                    newSection.getDownStationId(),
                    updatedPreviousDistance,
                    previousSection.getUpStationId(),
                    previousSection.getDownStationId(),
                    lineId);
            return;
        }
        throw new SectionDistanceException(newSection, previousSection);
    }

    private void validateDistanceAndUpdateDownStationId(Long lineId, Section newSection, Section followingSection) {
        if (newSection.isShorterThan(followingSection)) {
            int updatedFollowingDistance = followingSection.getDistance() - newSection.getDistance();
            sectionDao.updateDownStationId(
                    newSection.getUpStationId(),
                    updatedFollowingDistance,
                    followingSection.getUpStationId(),
                    followingSection.getDownStationId(),
                    lineId);
            return;
        }
        throw new SectionDistanceException(newSection, followingSection);
    }
}
