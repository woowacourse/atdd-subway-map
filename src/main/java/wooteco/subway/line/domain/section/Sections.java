package wooteco.subway.line.domain.section;

import java.util.ArrayList;
import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return new ArrayList(sections);
    }

}
