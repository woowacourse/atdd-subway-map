package wooteco.subway.line;

public class LineExistenceException extends RuntimeException {
    public LineExistenceException(String message) {
        super(message);
    }
}