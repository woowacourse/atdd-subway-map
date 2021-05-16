package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.repository.SectionRepository;
import wooteco.subway.line.domain.section.Sections;
import wooteco.subway.line.service.dto.section.SectionSaveDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void addSection(Long lineId, SectionSaveDto sectionSaveDto) {
        Sections sections = new Sections(sectionRepository.findByLineId(lineId));
        List<Section> tempSections = sections.toList();
        Section section = new Section(lineId, sectionSaveDto.getUpStationId(),
                sectionSaveDto.getDownStationId(), new Distance(sectionSaveDto.getDistance()));
        sections.add(section);
        List<Section> updatedSections = sections.toList();
        updateSectionByChanged(tempSections, updatedSections);
    }

    private void updateSectionByChanged(List<Section> sections, List<Section> updatedSections) {
        sections.stream()
                .filter(section -> !updatedSections.contains(section))
                .findAny().ifPresent(section -> sectionRepository.remove(section.getId()));

        updatedSections.stream()
                .filter(section -> !sections.contains(section))
                .collect(Collectors.toList())
                .forEach(sectionRepository::save);
    }
}
