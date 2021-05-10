package wooteco.subway.line.section;

import java.util.List;

public class Sections {

    private final List<Section> sectionGroup;

    public Sections(final List<Section> sectionGroup) {
        this.sectionGroup = sectionGroup;
    }

    public List<Section> toList() {
        return sectionGroup;
    }
}
