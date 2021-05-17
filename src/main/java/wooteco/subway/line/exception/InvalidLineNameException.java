package wooteco.subway.line.exception;

public class InvalidLineNameException extends Line4XXException {
    public InvalidLineNameException(String message) {
        super(message);
    }
}
