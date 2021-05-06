package wooteco.subway.exception;

public class DuplicateStationException extends RuntimeException {

    public DuplicateStationException(final String exceptionMessage) {
        super(exceptionMessage);
    }
}
