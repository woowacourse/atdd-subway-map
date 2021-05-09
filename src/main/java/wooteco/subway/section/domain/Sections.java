package wooteco.subway.section.domain;

import java.util.List;

public class Sections {
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> sections() {
        return sections;
    }
}