package wooteco.subway.exception;

public class NotFoundStationException extends RuntimeException {

    public NotFoundStationException(String errorMessage) {
        super(errorMessage);
    }
}
