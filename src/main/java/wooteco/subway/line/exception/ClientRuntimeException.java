package wooteco.subway.line.exception;

public class ClientRuntimeException extends IllegalArgumentException {

    public ClientRuntimeException(final String message) {
        super(message);
    }
}
