package wooteco.subway.common;

public class ResponseError {
    final String message;

    public ResponseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
