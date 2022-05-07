package wooteco.subway.exception;

public class DataDuplicationException extends IllegalArgumentException {

    private final String message;

    public DataDuplicationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
