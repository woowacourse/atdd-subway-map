package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.IllegalSectionException;

public class Sections {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        isPossibleRegistration(section);
        preventFork(section);

        sections.add(section);
    }

    private void isPossibleRegistration(Section section) {
        sections.stream()
                .filter(s -> s.isContainStation(section))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("구간 등록이 불가능합니다."));
    }

    private void preventFork(Section newSection) {
        final Optional<Section> findSection = findForkSection(newSection);
        findSection.ifPresent(section -> processFork(section, newSection));
    }

    private Optional<Section> findForkSection(Section section) {
        return sections.stream()
                .filter(s -> s.isFork(section))
                .findAny();
    }

    private void processFork(Section existingSection, Section newSection) {
        final Section section = new Section(existingSection.getId(), existingSection.getLineId(), newSection.getDownStationId(),
                existingSection.getDownStationId(), existingSection.getDistance() - newSection.getDistance());

        sections.add(section);
        sections.remove(existingSection);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
