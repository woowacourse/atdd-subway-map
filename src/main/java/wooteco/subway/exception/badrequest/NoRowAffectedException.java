package wooteco.subway.exception.badrequest;

import org.springframework.dao.DataAccessException;

public class NoRowAffectedException extends BadRequest {
    public NoRowAffectedException() {
    }

    public NoRowAffectedException(String msg) {
        super(msg);
    }

    public NoRowAffectedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
