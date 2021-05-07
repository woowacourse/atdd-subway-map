package wooteco.subway.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateNameException extends DuplicateKeyException {
    public DuplicateNameException(final String msg) {
        super(msg);
    }
}
