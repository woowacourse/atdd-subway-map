package wooteco.subway.exception.service;

public class ValidationFailureException extends BusinessException {

    public ValidationFailureException(final String message) {
        super(message);
    }
}
