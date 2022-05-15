package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.SectionServiceDeleteRequest;
import wooteco.subway.service.dto.SectionServiceRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Long save(SectionServiceRequest sectionServiceRequest, Long lineId) {
        Section section = toSection(sectionServiceRequest, lineId);
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        if (sections.isMiddleSection(section)) {
            return updateMiddleSection(section, sections);
        }

        return sectionDao.save(section);
    }

    private Section toSection(SectionServiceRequest sectionRequest, Long lineId) {
        return new Section(lineId, sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    private Long updateMiddleSection(Section section, Sections sections) {
        if (sections.hasStationId(section.getDownStationId())) {
            return updateUpStationSection(section, sections);
        }
        return updateDownStationSection(section, sections);
    }

    private Long updateUpStationSection(Section section, Sections sections) {
        Section updateSection = sections.findSectionByDownStationId(section.getDownStationId());
        if (updateSection.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("등록할 구간의 길이가 기존 역 사이의 길이보다 길거나 같으면 안됩니다.");
        }
        sectionDao.update(updateSection.getId(), section.getUpStationId(),
            updateSection.getDistance() - section.getDistance());

        return sectionDao.save(
            new Section(section.getLineId(), section.getUpStationId(),
                updateSection.getDownStationId(), section.getDistance()));
    }

    private Long updateDownStationSection(Section section, Sections sections) {
        Section updateSection = sections.findSectionByUpStationId(section.getUpStationId());

        if (updateSection.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("등록할 구간의 길이가 기존 역 사이의 길이보다 길거나 같으면 안됩니다.");
        }

        sectionDao.update(updateSection.getId(), section.getDownStationId(),
            section.getDistance());

        return sectionDao.save(new Section(section.getLineId(), section.getDownStationId(),
            updateSection.getDownStationId(),
            updateSection.getDistance() - section.getDistance()));
    }

    @Transactional
    public boolean removeSection(SectionServiceDeleteRequest sectionServiceDeleteRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(sectionServiceDeleteRequest.getLineId()));

        validateRemoveSection(sections);

        return deleteSection(sectionServiceDeleteRequest, sections);
    }

    private void validateRemoveSection(Sections sections) {
        if (!sections.canRemoveSection()) {
            throw new IllegalArgumentException("구간을 제거할 수 없는 상태입니다.");
        }
    }

    private boolean deleteSection(SectionServiceDeleteRequest sectionServiceDeleteRequest, Sections sections) {
        if (isEndStationSection(sectionServiceDeleteRequest, sections)) {
            Section upStationSection = sections.findSectionByUpStationId(
                sectionServiceDeleteRequest.getStationId());
            return sectionDao.deleteById(upStationSection.getId());
        }

        Section downStationSection = sections.findSectionByDownStationId(
            sectionServiceDeleteRequest.getStationId());
        Section deleteSectionStation = sections.findSectionByUpStationId(
            sectionServiceDeleteRequest.getStationId());

        int totalDistance = downStationSection.getDistance() + deleteSectionStation.getDistance();
        sectionDao.update(downStationSection.getId(), deleteSectionStation.getDownStationId(),
            totalDistance);
        return sectionDao.deleteById(deleteSectionStation.getId());
    }

    private boolean isEndStationSection(SectionServiceDeleteRequest sectionServiceDeleteRequest,
        Sections sections) {
        return !sections.isMiddleSection(
            new Section(sectionServiceDeleteRequest.getStationId(), sectionServiceDeleteRequest.getStationId()));
    }
}
