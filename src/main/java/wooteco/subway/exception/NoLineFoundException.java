package wooteco.subway.exception;

public class NoLineFoundException extends RuntimeException {

    private static final String NOT_FOUND_MESSAGE = "요청한 노선이 존재하지 않습니다.";

    public NoLineFoundException() {
        super(NOT_FOUND_MESSAGE);
    }
}
