package wooteco.subway.exception.notfound;

import wooteco.subway.exception.ExceptionMessage;

public class SectionNotFoundException extends NotFoundException {
    public SectionNotFoundException() {
        super(ExceptionMessage.NOT_FOUND_SECTION.getContent());
    }
}
