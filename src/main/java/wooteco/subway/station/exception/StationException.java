package wooteco.subway.station.exception;

public class StationException extends RuntimeException {
    private ErrorCode errorCode;

    public StationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int statusCode() {
        return errorCode.getStatusCode();
    }
}
