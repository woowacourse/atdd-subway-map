package wooteco.subway.exception;

public class DuplicatedLineNameException extends RuntimeException {

    public DuplicatedLineNameException(final String errorMessage) {
        super(errorMessage);
    }
}
