package wooteco.subway.domain;

import java.util.LinkedList;

public class Sections {

    private final LinkedList<Section> sections;

    public Sections(LinkedList<Section> sections) {
        this.sections = sections;
    }

    public Sections(Section section) {
        this.sections = new LinkedList<>();
        sections.add(section);
    }

    public Station findUpperTerminal() {
        return sections.get(0).getUp();
    }

    public Station findBottomTerminal() {
        return sections.get(-1).getDown();
    }
}
