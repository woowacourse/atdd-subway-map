package wooteco.subway.line;

public class LineException extends IllegalArgumentException {

    public LineException() {
        super("유효하지 않은 노선입니다.");
    }

    public LineException(String s) {
        super(s);
    }

    public LineException(String message, Throwable cause) {
        super(message, cause);
    }

    public LineException(Throwable cause) {
        super(cause);
    }
}
