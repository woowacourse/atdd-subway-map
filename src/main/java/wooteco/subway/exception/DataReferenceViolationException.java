package wooteco.subway.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DataReferenceViolationException extends DataIntegrityViolationException {

    private final String message;

    public DataReferenceViolationException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
