package wooteco.subway.exception;

public class NotFoundStationException extends RuntimeException {

    public NotFoundStationException(final String errorMessage) {
        super(errorMessage);
    }
}
