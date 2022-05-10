package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.ServiceDtoAssembler;
import wooteco.subway.service.dto.section.SectionRequestDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long create(SectionRequestDto sectionRequestDto) {
        Section newSection = ServiceDtoAssembler.Section(sectionRequestDto);
        Sections sections = findAllByLineId(newSection.getLineId());
        sections.validateAddNewSection(newSection);

        if (sections.isUpStationId(newSection.getUpStationId())) {
            updateUpStationId(sections, newSection);
        }
        if (sections.isDownStationId(newSection.getDownStationId())) {
            updateDownStationId(sections, newSection);
        }

        return sectionDao.create(newSection).getId();
    }

    public Sections findAllByLineId(Long lineId) {
        return new Sections(sectionDao.findAllByLineId(lineId));
    }

    private void updateUpStationId(Sections sections, Section newSection) {
        Section upStationIdSection = sections.getSectionByUpStationId(newSection.getUpStationId());
        validateDistance(upStationIdSection, newSection);
        int calculateDistance = upStationIdSection.minusDistance(newSection);
        sectionDao.updateUpStationId(
                upStationIdSection.getId(),
                newSection.getDownStationId(),
                calculateDistance
        );
    }

    private void updateDownStationId(Sections sections, Section newSection) {
        Section downStationIdSection = sections.getSectionByDownStationId(newSection.getDownStationId());
        validateDistance(downStationIdSection, newSection);
        int calculateDistance = downStationIdSection.minusDistance(newSection);
        sectionDao.updateDownStationId(
                downStationIdSection.getId(),
                newSection.getUpStationId(),
                calculateDistance
        );
    }

    private void validateDistance(Section baseSection, Section newSection) {
        if (baseSection.checkNotUnderDistance(newSection)) {
            throw new IllegalArgumentException("[ERROR] 기존 구간 거리보다 작아야합니다.");
        }
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = findAllByLineId(lineId);
        sections.validateSize();

        if (sections.hasOneStation(stationId)) {
            deleteOneStation(sections, stationId);
            return;
        }
        deleteMiddleStation(sections, lineId, stationId);
    }

    private void deleteOneStation(Sections sections, Long stationId) {
        Long sectionId = sections.getSectionId(stationId);
        sectionDao.delete(sectionId);
    }

    private void deleteMiddleStation(Sections sections, Long lineId, Long stationId) {
        Section firstSection = sections.getUpStationSection(stationId);
        Section secondSection = sections.getDownStationSection(stationId);
        int newDistance = firstSection.getDistance() + secondSection.getDistance();
        Section newSection = new Section(lineId, firstSection.getUpStationId(), secondSection.getDownStationId(), newDistance);

        sectionDao.delete(firstSection.getId());
        sectionDao.delete(secondSection.getId());
        sectionDao.create(newSection);
    }
}
