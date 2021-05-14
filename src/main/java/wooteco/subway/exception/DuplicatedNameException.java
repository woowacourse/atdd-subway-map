package wooteco.subway.exception;

import wooteco.subway.line.exception.ClientRuntimeException;

public class DuplicatedNameException extends ClientRuntimeException {

    public DuplicatedNameException(final String message) {
        super(message);
    }
}
