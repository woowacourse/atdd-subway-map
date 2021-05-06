package wooteco.subway.exception;

import org.springframework.dao.DataAccessException;

public class NoSuchLineException extends DataAccessException {

    public NoSuchLineException(String msg) {
        super(msg);
    }
}
