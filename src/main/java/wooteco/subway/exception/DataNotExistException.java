package wooteco.subway.exception;

public class DataNotExistException extends IllegalArgumentException {

    private final String message;

    public DataNotExistException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
