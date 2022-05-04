package wooteco.subway.exception;

import org.springframework.dao.DuplicateKeyException;

public class LineDuplicateException extends DuplicateKeyException {
    public LineDuplicateException(final String msg) {
        super(msg);
    }
}
