package wooteco.subway.section.domain;

import wooteco.subway.section.exception.SectionsSizeTooSmallException;

import java.util.LinkedList;
import java.util.List;

public class Sections {
    private static final int MINIMUM_SIZE = 2;
    private final LinkedList<Section> sections;

    public Sections(List<Section> sections) {
        this(new LinkedList<>(sections));
    }

    public Sections(LinkedList<Section> sections) {
        checkMinimumSize(sections);
        this.sections = sections;
    }

    private void checkMinimumSize(List<Section> sections) {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new SectionsSizeTooSmallException(String.format("최소 %d 이상이어야 합니다. 현재 사이즈 : %d, ", MINIMUM_SIZE, sections.size()));
        }
    }
}
