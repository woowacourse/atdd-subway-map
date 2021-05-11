package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.section.request.SectionInsertRequest;
import wooteco.subway.dto.section.response.SectionInsertResponse;
import wooteco.subway.exception.section.SectionDistanceException;
import wooteco.subway.repository.dao.SectionDao;

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

    private void validateDistanceAndUpdateUpStationId(Long lineId, Section newSection, Section previousSection) {
        if (newSection.isShorterThan(previousSection)) {
            sectionDao.updateUpStationId(
                    newSection.getDownStationId(),
                    previousSection.getUpStationId(),
                    previousSection.getDownStationId(),
                    lineId);
            return;
        }
        throw new SectionDistanceException(newSection, previousSection);
    }

    private void validateDistanceAndUpdateDownStationId(Long lineId, Section newSection, Section followingSection) {
        if (newSection.isShorterThan(followingSection)) {
            sectionDao.updateDownStationId(
                    newSection.getUpStationId(),
                    followingSection.getUpStationId(),
                    followingSection.getDownStationId(),
                    lineId);
            return;
        }
        throw new SectionDistanceException(newSection, followingSection);
    }
}
