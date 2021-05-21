package wooteco.subway.exception.badrequest;

public class BadRequest extends RuntimeException {

    public BadRequest() {
    }

    public BadRequest(String message) {
        super(message);
    }

    public BadRequest(String message, Throwable cause) {
        super(message, cause);
    }
}
