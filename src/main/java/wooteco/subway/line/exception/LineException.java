package wooteco.subway.line.exception;

public class LineException extends RuntimeException {
    private ErrorCode errorCode;

    public LineException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int statusCode() {
        return errorCode.getStatusCode();
    }
}
