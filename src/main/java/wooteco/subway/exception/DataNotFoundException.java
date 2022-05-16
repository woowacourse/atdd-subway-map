package wooteco.subway.exception;

public class DataNotFoundException extends IllegalArgumentException {

    private final String message;

    public DataNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
