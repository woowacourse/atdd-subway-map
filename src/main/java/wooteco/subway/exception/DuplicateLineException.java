package wooteco.subway.exception;

public class DuplicateLineException extends RuntimeException {

    public DuplicateLineException() {
    }

    public DuplicateLineException(String message) {
        super(message);
    }
}
