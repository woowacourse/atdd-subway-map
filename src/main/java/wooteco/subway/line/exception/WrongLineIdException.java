package wooteco.subway.line.exception;

public class WrongLineIdException extends Line4XXException {
    public WrongLineIdException(String message) {
        super(message);
    }
}
