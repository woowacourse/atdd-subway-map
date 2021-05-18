package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.controller.dto.SectionRequest;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.repository.SectionRepository;
import wooteco.subway.line.domain.section.Sections;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionRepository.findByLineId(lineId));
        List<Section> tempSections = sections.toList();
        Section section = new Section(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), new Distance(sectionRequest.getDistance()));
        sections.add(section);
        List<Section> updatedSections = sections.toList();
        updateSectionByChanged(tempSections, updatedSections);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionRepository.findByLineId(lineId));
        List<Section> tempSections = sections.toList();
        sections.delete(stationId);
        List<Section> deletedSections = sections.toList();
        updateSectionByChanged(tempSections, deletedSections);
    }

    private void updateSectionByChanged(List<Section> sections, List<Section> updatedSections) {
        sections.stream()
                .filter(section -> !updatedSections.contains(section))
                .collect(Collectors.toList())
                .forEach(section -> sectionRepository.delete(section.getId()));

        updatedSections.stream()
                .filter(section -> !sections.contains(section))
                .collect(Collectors.toList())
                .forEach(sectionRepository::save);
    }
}
