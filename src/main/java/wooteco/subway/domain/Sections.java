package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.SectionDuplicateException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
    }

    private void validateDuplicateSection(final Section checkSection) {
        boolean isDuplicate = sections.stream()
                .anyMatch(section -> section.isDuplicateSection(checkSection));
        if (isDuplicate) {
            throw new SectionDuplicateException();
        }
    }
}
