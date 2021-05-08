package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(Section section) {
        checkAbleToAddSection(section);
        sections.add(section);
    }

    private void checkAbleToAddSection(Section section) {
        if (!isOnlyOneRegistered(section)) {
            throw new IllegalStateException("[ERROR] 노선에 등록할 구간의 역이 하나만 등록되어 있어야 합니다.");
        }
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
