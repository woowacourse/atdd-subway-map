package wooteco.subway.exception;

public class CustomException extends RuntimeException {

    private final String message;

    public CustomException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
