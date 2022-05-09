package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.utils.exception.SectionCreateException;

public class Sections {

    private static final String SECTION_ALREADY_EXIST_MESSAGE = "이미 존재하는 구간입니다.";

    private List<Section> values;

    public Sections(final List<Section> values) {
        this.values = values;
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
    }

    private void validateDuplicateSection(final Section section) {
        values.stream()
                .filter(value -> value.equals(section))
                .findAny()
                .ifPresent(value -> {
                    throw new SectionCreateException(SECTION_ALREADY_EXIST_MESSAGE);
                });
    }
}
