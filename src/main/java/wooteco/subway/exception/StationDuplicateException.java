package wooteco.subway.exception;

import org.springframework.dao.DuplicateKeyException;

public class StationDuplicateException extends DuplicateKeyException {
    public StationDuplicateException(final String msg) {
        super(msg);
    }
}
