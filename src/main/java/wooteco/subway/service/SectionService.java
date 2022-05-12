package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionRepository;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.service.dto.section.SectionRequest;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void create(Long lineId, SectionRequest sectionRequest) {
        Sections sections = findSections(lineId);
        sections.append(createSection(sectionRequest));
        sectionRepository.updateSections(lineId, sections);
    }

    private Section createSection(SectionRequest sectionRequest) {
        return new Section(
                sectionRepository.findStationById(sectionRequest.getUpStationId()),
                sectionRepository.findStationById(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        Sections sections = findSections(lineId);
        sections.remove(sectionRepository.findStationById(stationId));
        sectionRepository.updateSections(lineId, sections);
    }

    private Sections findSections(Long lineId) {
        return new Sections(sectionRepository.findSectionsByLineId(lineId));
    }
}
