package wooteco.subway.domain.exception;

public class BlankArgumentException extends IllegalArgumentException {

    public BlankArgumentException(String message) {
        super(message);
    }
}
