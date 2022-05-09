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

    public int size() {
        return sections.size();
    }
}
