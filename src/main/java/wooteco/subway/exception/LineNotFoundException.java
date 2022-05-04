package wooteco.subway.exception;

import org.springframework.dao.EmptyResultDataAccessException;

public class LineNotFoundException extends EmptyResultDataAccessException {

    public LineNotFoundException(final String msg, final int expectedSize) {
        super(msg, expectedSize);
    }
}
