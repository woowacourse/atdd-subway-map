package wooteco.subway.line.exception;

public class WrongLineInformationException extends Line4XXException {
    public WrongLineInformationException(String message) {
        super(message);
    }
}
