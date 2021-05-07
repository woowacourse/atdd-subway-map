package wooteco.subway.exception;

public class SubwayException extends RuntimeException {

    private final int statusCode;

    public SubwayException(ExceptionStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.statusCode = exceptionStatus.getStatusCode();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
