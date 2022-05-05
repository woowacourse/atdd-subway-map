package wooteco.subway.exception;

public class InvalidSubwayResourceException extends RuntimeException {

    private final String message;

    public InvalidSubwayResourceException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
