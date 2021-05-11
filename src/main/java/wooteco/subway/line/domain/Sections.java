package wooteco.subway.line.domain;

import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> toList() {
        return sections;
    }
}
