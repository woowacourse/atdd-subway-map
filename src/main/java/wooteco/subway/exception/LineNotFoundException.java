package wooteco.subway.exception;

public class LineNotFoundException extends RuntimeException {

    private static final String message = "일치하는 노선을 찾을 수 없습니다.";

    public LineNotFoundException() {
        super(message);
    }
}
