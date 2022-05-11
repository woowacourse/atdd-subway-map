package wooteco.subway.exception;

public class NotFoundLineException extends RuntimeException {

    public NotFoundLineException() {
        super("노선을 찾을 수 없습니다.");
    }
}
