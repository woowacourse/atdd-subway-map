package wooteco.subway.line;

public class LineException extends IllegalArgumentException {

    public LineException() {
        super("유효하지 않은 노선입니다.");
    }

    public LineException(final String s) {
        super(s);
    }
}
