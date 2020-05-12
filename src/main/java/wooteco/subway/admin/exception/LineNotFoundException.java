package wooteco.subway.admin.exception;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException() {
        super("노선이 존재하지 않습니다.");
    }
}
