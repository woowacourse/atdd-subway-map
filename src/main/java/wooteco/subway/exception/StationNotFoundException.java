package wooteco.subway.exception;

import org.springframework.dao.EmptyResultDataAccessException;

public class StationNotFoundException extends EmptyResultDataAccessException {
    public StationNotFoundException(final String msg, final int expectedSize) {
        super(msg, expectedSize);
    }
}
