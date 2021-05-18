package wooteco.subway.exception;

import org.springframework.dao.DataAccessException;

public class NoRowAffectedException extends DataAccessException {
    public NoRowAffectedException(String msg) {
        super(msg);
    }

    public NoRowAffectedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
