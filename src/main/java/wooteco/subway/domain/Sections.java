package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {

    private static final int FIRST_INDEX = 0;

    private final List<Section> sections;

    public static Sections create(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections create() {
        return new Sections(new ArrayList<>());
    }


    public void addSection(Section section) {
        sections.add(section);
    }

    public Section firstSection() {
        return sections.get(FIRST_INDEX);
    }
}
