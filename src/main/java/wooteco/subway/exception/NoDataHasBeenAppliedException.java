package wooteco.subway.exception;

import org.springframework.dao.DataAccessException;

public class NoDataHasBeenAppliedException extends DataAccessException {
    public NoDataHasBeenAppliedException(String msg) {
        super(msg);
    }

    public NoDataHasBeenAppliedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
