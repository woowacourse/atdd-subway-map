package wooteco.subway.station.exception;

import org.springframework.dao.DataAccessException;

public class NoSuchStationException extends DataAccessException {
    public NoSuchStationException(String msg) {
        super(msg);
    }
}
