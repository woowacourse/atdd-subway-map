package wooteco.subway.line.exception;

public class LineExistenceException extends LineException {
    private final static String message = "존재하는 노선 이름입니다.";

    public LineExistenceException() {
        super(message);
    }
}
