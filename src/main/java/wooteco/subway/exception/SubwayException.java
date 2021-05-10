package wooteco.subway.exception;

public class SubwayException extends RuntimeException{

    public SubwayException(String message) {
        super(message);
    }

    public SubwayException(String message, Throwable cause) {
        super(message, cause);
    }
}
