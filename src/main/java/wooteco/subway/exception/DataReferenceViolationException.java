package wooteco.subway.exception;

public class DataReferenceViolationException extends IllegalArgumentException {

    private final String message;

    public DataReferenceViolationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
