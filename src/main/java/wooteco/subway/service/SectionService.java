package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.ui.dto.SectionDeleteRequest;
import wooteco.subway.ui.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long save(SectionRequest sectionRequest) {
        Section section = sectionRequest.toSection();

        Sections sections = new Sections(sectionDao.findByLineId(sectionRequest.getLineId()));

        if (sections.isMiddleSection(section)) {
            sections.validateForkedLoad(section);
            return updateMiddleSection(section, sections);
        }

        return sectionDao.save(section);
    }

    private Long updateMiddleSection(Section section, Sections sections) {
        Section upStationSection = sections.findSectionByUpStationId(section.getUpStationId());

        if (upStationSection.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("등록할 구간의 길이가 기존 역 사이의 길이보다 길거나 같으면 안됩니다.");
        }

        sectionDao.update(upStationSection.getId(), section.getDownStationId(),
            section.getDistance());

        return sectionDao.save(
            new Section(section.getLineId(), section.getUpStationId(),
                section.getDownStationId(),
                upStationSection.getDistance() - section.getDistance()));
    }

    public boolean removeSection(SectionDeleteRequest sectionDeleteRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(sectionDeleteRequest.getLineId()));

        validateRemoveSection(sectionDeleteRequest, sections);

        Section upStationSection = sections.findSectionByDownStationId(sectionDeleteRequest.getStationId());

        Section deleteSectionStation = sections.findSectionByUpStationId(sectionDeleteRequest.getStationId());

        int totalDistance = upStationSection.getDistance() + deleteSectionStation.getDistance();
        sectionDao.update(upStationSection.getId(), deleteSectionStation.getDownStationId(),
            totalDistance);
        return sectionDao.deleteById(deleteSectionStation.getId());
    }

    private void validateRemoveSection(SectionDeleteRequest sectionDeleteRequest,
        Sections sections) {
        if (!sections.isMiddleSection(new Section(sectionDeleteRequest.getStationId(),
            sectionDeleteRequest.getStationId()))) {
            throw new IllegalArgumentException("종점은 제거할 수 없습니다.");
        }
    }
}
