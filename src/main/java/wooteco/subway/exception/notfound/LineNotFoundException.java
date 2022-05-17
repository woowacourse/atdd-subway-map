package wooteco.subway.exception.notfound;

import wooteco.subway.exception.ExceptionMessage;

public class LineNotFoundException extends NotFoundException {
    public LineNotFoundException() {
        super(ExceptionMessage.NOT_FOUND_LINE.getContent());
    }
}
