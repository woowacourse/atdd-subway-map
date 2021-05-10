package wooteco.subway.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateException extends DuplicateKeyException {
    public DuplicateException(final String msg) {
        super(msg);
    }
}
