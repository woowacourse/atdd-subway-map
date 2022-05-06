package wooteco.subway.exception;

public class DuplicateStationException extends RuntimeException {

    public DuplicateStationException() {
    }

    public DuplicateStationException(String message) {
        super(message);
    }
}
