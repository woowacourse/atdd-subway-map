package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.ExceptionMessage;

public class NearSections {

    private static final int MAX_NEAR_SECTION_COUNT = 2;

    private final List<Section> sections;

    public NearSections(List<Section> sections) {
        if (sections.size() > MAX_NEAR_SECTION_COUNT) {
            throw new IllegalArgumentException(ExceptionMessage.NEAR_SECTIONS_OVER_SIZE.getContent());
        }
        this.sections = sections;
    }
}
