package wooteco.subway.exception;

import wooteco.subway.line.exception.ClientRuntimeException;

public class DataNotFoundException extends ClientRuntimeException {

    public DataNotFoundException(final String message) {
        super(message);
    }
}
