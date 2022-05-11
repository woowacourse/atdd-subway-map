package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.ui.dto.SectionDeleteRequest;
import wooteco.subway.ui.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long save(SectionRequest sectionRequest, Long lineId) {
        Section section = toSection(sectionRequest, lineId);
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        if (sections.isMiddleSection(section)) {
            return updateMiddleSection(section, sections);
        }

        return sectionDao.save(section);
    }

    private Section toSection(SectionRequest sectionRequest, Long lineId) {
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

    public boolean removeSection(SectionDeleteRequest sectionDeleteRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(sectionDeleteRequest.getLineId()));

        validateRemoveSection(sections);

        return deleteSection(sectionDeleteRequest, sections);
    }

    private void validateRemoveSection(Sections sections) {
        if (sections.isSingleSection()) {
            throw new IllegalArgumentException("구간이 하나 밖에 없어서 제거할 수 없습니다.");
        }
    }

    private boolean deleteSection(SectionDeleteRequest sectionDeleteRequest, Sections sections) {
        if (isEndStationSection(sectionDeleteRequest, sections)) {
            Section upStationSection = sections.findSectionByUpStationId(
                sectionDeleteRequest.getStationId());
            return sectionDao.deleteById(upStationSection.getId());
        }

        Section downStationSection = sections.findSectionByDownStationId(
            sectionDeleteRequest.getStationId());
        Section deleteSectionStation = sections.findSectionByUpStationId(
            sectionDeleteRequest.getStationId());

        int totalDistance = downStationSection.getDistance() + deleteSectionStation.getDistance();
        sectionDao.update(downStationSection.getId(), deleteSectionStation.getDownStationId(),
            totalDistance);
        return sectionDao.deleteById(deleteSectionStation.getId());
    }

    private boolean isEndStationSection(SectionDeleteRequest sectionDeleteRequest,
        Sections sections) {
        return !sections.isMiddleSection(
            new Section(sectionDeleteRequest.getStationId(), sectionDeleteRequest.getStationId()));
    }
}
