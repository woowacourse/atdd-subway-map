package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.repository.SectionRepository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public SectionResponse createSection(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRepository.createSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Long sectionId = sectionRepository.insert(lineId, section);
        return new SectionResponse(sectionId, lineId, section);
    }

    @Transactional
    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRepository.createSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Sections sections = sectionRepository.loadSections(lineId);
        sections.validateIfPossibleToInsert(section);

        updateExistingSections(lineId, section, sections);
        Long sectionId = sectionRepository.insert(lineId, section);
        return new SectionResponse(sectionId, lineId, section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = sectionRepository.loadSections(lineId);
        sections.validateIfPossibleToDelete();

        if (sections.hasStationAsDownward(stationId) && sections.hasStationAsUpward(stationId)) {
            sectionRepository.delete(lineId, stationId);
            sectionRepository.insert(lineId, sections.createMergedSectionAfterDeletion(stationId));
            return;
        }

        if (sections.hasStationAsDownward(stationId)) {
            sectionRepository.deleteBottomSection(lineId, sections.getBottomSection());
        }

        if (sections.hasStationAsUpward(stationId)) {
            sectionRepository.deleteTopSection(lineId, sections.getTopSection());
        }
    }

    private void updateExistingSections(Long lineId, Section section, Sections sections) {
        if (sections.isNewStationDownward(section)) {
            sectionRepository.updateWhenNewStationDownward(lineId, section);
        }
        sectionRepository.updateWhenNewStationUpward(lineId, section);
    }

    public Sections loadSections(Long lineId) {
        return sectionRepository.loadSections(lineId);
    }
}
