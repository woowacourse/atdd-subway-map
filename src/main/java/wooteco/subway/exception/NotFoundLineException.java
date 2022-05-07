package wooteco.subway.exception;

public class NotFoundLineException extends RuntimeException {

    private static final String NOT_FOUND_MESSAGE = "요청한 노선이 존재하지 않습니다";

    public NotFoundLineException() {
        super(NOT_FOUND_MESSAGE);
    }

    public NotFoundLineException(Long id) {
        super(NOT_FOUND_MESSAGE + " : " + id);
    }
}
