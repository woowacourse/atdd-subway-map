package wooteco.subway.line.exception;

public class NoneExistentStationException extends ClientRuntimeException {

    public NoneExistentStationException(final String message) {
        super(message);
    }
}
