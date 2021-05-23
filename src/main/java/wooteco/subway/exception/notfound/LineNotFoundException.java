package wooteco.subway.exception.notfound;

import wooteco.subway.exception.badrequest.BadRequest;

public class LineNotFoundException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 지하철 노선 입니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }

    public LineNotFoundException(final String message) {
        super(message);
    }

    public LineNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
