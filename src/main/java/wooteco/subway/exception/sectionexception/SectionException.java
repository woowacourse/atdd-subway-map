package wooteco.subway.exception.sectionexception;

import wooteco.subway.section.model.Section;

public class SectionException extends RuntimeException{
    public SectionException(String message) {
        super(message);
    }
}
