package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(Section section) {
        sections.add(section);
    }

    public boolean isOnlyOneRegistered(Section anotherSection) {
        boolean hasUpStation = sections.stream()
                .anyMatch(section -> section.has(anotherSection.upStation()));
        boolean hasDownStation = sections.stream()
                .anyMatch(section -> section.has(anotherSection.downStation()));
        return (hasUpStation && !hasDownStation) || (!hasUpStation && hasDownStation);
    }

    public List<Section> sections() {
        return sections;
    }
}
