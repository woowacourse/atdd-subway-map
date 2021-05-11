package wooteco.subway.section.exception;

import java.util.NoSuchElementException;

public class SectionsHasNotSectionException extends NoSuchElementException {
    public SectionsHasNotSectionException(String msg) {
        super(msg);
    }
}
