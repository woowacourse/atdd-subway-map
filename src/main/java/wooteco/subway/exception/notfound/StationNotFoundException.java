package wooteco.subway.exception.notfound;

import wooteco.subway.exception.badrequest.BadRequest;

public class StationNotFoundException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 지하철 역 입니다.";
    public StationNotFoundException() {
        super(MESSAGE);
    }

    public StationNotFoundException(final String message) {
        super(message);
    }

    public StationNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
